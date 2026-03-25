package ttLogistics.controllers;

import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class DriverDashboard extends JFrame {

    DefaultTableModel delivModel = new DefaultTableModel();
    DefaultTableModel statsModel = new DefaultTableModel();
    JTable delivTable, statsTable;
    String driverName = "", driverType = "";
    JLabel[] statValues = new JLabel[3];
    String lastDelivFilter = "ALL";

    public DriverDashboard() {
        setTitle("TT Logistics Driver Portal");
        setSize(980, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG);
        setLayout(new BorderLayout());
        fetchMeta();

        JPanel topHdr = new JPanel(new BorderLayout());
        topHdr.setBackground(UIHelper.PANEL);
        topHdr.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        JLabel lt = new JLabel("Driver Portal");
        lt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lt.setForeground(UIHelper.GOLD);
        JLabel lu = new JLabel(driverName + "  [" + driverType + "]  ");
        lu.setFont(UIHelper.BODY);
        lu.setForeground(UIHelper.DIM);
        topHdr.add(lt, BorderLayout.WEST);
        topHdr.add(lu, BorderLayout.EAST);
        add(topHdr, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UIHelper.BG);
        tabs.setForeground(UIHelper.TEXT);
        tabs.setFont(UIHelper.LBL);

        JPanel delPanel = new JPanel(new BorderLayout());
        delPanel.setBackground(UIHelper.BG);
        delivTable = UIHelper.styledTable(delivModel);
        delPanel.add(UIHelper.scrollFor(delivTable), BorderLayout.CENTER);

        JButton btnAll    = UIHelper.btn("All Deliveries", UIHelper.GOLD);
        JButton btnActive = UIHelper.btn("Active", UIHelper.ACCENT);
        JButton btnDone   = UIHelper.btn("Completed", UIHelper.DIM);
        JButton btnMark   = UIHelper.btn("Mark as Completed", new Color(0, 160, 100));
        JButton btnRefreshDeliv = UIHelper.btn("Refresh", UIHelper.BLUE);

        JLabel markInfo = new JLabel("  Select an active delivery row, then click Mark as Completed");
        markInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        markInfo.setForeground(UIHelper.DIM);

        JPanel delBottom = new JPanel(new BorderLayout());
        delBottom.setBackground(UIHelper.PANEL);
        delBottom.add(UIHelper.bar(btnAll, btnActive, btnDone, btnMark, btnRefreshDeliv), BorderLayout.WEST);
        delBottom.add(markInfo, BorderLayout.CENTER);
        delPanel.add(delBottom, BorderLayout.SOUTH);

        btnAll.addActionListener(e    -> { lastDelivFilter = "ALL";    loadDeliveries("ALL"); });
        btnActive.addActionListener(e -> { lastDelivFilter = "ACTIVE"; loadDeliveries("ACTIVE"); });
        btnDone.addActionListener(e   -> { lastDelivFilter = "DONE";   loadDeliveries("DONE"); });
        btnRefreshDeliv.addActionListener(e -> loadDeliveries(lastDelivFilter));

        btnMark.addActionListener(e -> {
            int selectedRow = delivTable.getSelectedRow();
            if (selectedRow < 0) { UIHelper.err("Select an active delivery row first."); return; }
            Object rawId     = delivModel.getValueAt(selectedRow, 0);
            Object rawStatus = delivModel.getValueAt(selectedRow, 4);
            if (rawStatus != null && rawStatus.toString().equals("Completed")) {
                UIHelper.err("This delivery is already Completed."); return;
            }
            int did = (int) rawId;
            int confirm = JOptionPane.showConfirmDialog(this,
                "Mark Delivery #" + did + " as Completed?\nThis cannot be undone.",
                "Confirm Completion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement ps = DBConnection.conn.prepareStatement(
                        "UPDATE Delivery SET delivery_status='Completed' WHERE delivery_id=?");
                    ps.setInt(1, did);
                    ps.executeUpdate();
                    ps.close();
                    UIHelper.info("Delivery #" + did + " marked as Completed!");
                    loadDeliveries(lastDelivFilter);
                    loadStats();
                } catch (Exception ex) { UIHelper.err("Update error:\n" + ex.getMessage()); }
            }
        });

        tabs.addTab("My Deliveries", delPanel);

        JPanel statPanel = new JPanel(new BorderLayout(0, 12));
        statPanel.setBackground(UIHelper.BG);
        statPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setBackground(UIHelper.BG);
        String[] sLbls = {"Total Deliveries", "Total Hours Worked", "Active Deliveries"};
        Color[]  sCols  = {UIHelper.GOLD, UIHelper.ACCENT, UIHelper.BLUE};
        for (int i = 0; i < 3; i++) {
            JPanel sc = new JPanel(new BorderLayout());
            sc.setBackground(UIHelper.CARD);
            sc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(sCols[i], 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
            statValues[i] = new JLabel("—", SwingConstants.CENTER);
            statValues[i].setFont(new Font("Segoe UI", Font.BOLD, 30));
            statValues[i].setForeground(sCols[i]);
            JLabel sl = new JLabel(sLbls[i], SwingConstants.CENTER);
            sl.setFont(UIHelper.BODY);
            sl.setForeground(UIHelper.DIM);
            sc.add(statValues[i], BorderLayout.CENTER);
            sc.add(sl, BorderLayout.SOUTH);
            cards.add(sc);
        }
        statPanel.add(cards, BorderLayout.NORTH);
        statsTable = UIHelper.styledTable(statsModel);
        statPanel.add(UIHelper.scrollFor(statsTable), BorderLayout.CENTER);
        JButton btnRefreshStats = UIHelper.btn("Refresh", UIHelper.GOLD);
        btnRefreshStats.addActionListener(e -> loadStats());
        statPanel.add(UIHelper.bar(btnRefreshStats), BorderLayout.SOUTH);
        tabs.addTab("My Stats", statPanel);

        tabs.addTab("My Profile", buildReadOnlyProfile());

        add(tabs, BorderLayout.CENTER);

        JPanel bot = new JPanel(new BorderLayout());
        bot.setBackground(UIHelper.PANEL);
        JLabel status = new JLabel("  Driver Access  •  View deliveries & mark complete");
        status.setFont(UIHelper.BODY);
        status.setForeground(UIHelper.GOLD);
        status.setBackground(UIHelper.PANEL);
        status.setOpaque(true);
        status.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        JButton btnOut = UIHelper.btn("Logout", UIHelper.RED);
        btnOut.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btnOut.addActionListener(e -> UIHelper.doLogout(this));
        bot.add(status, BorderLayout.WEST);
        bot.add(btnOut, BorderLayout.EAST);
        add(bot, BorderLayout.SOUTH);

        loadDeliveries("ALL");
        loadStats();
        setVisible(true);
    }

    void fetchMeta() {
        try {
            PreparedStatement ps = DBConnection.conn.prepareStatement(
                "SELECT p.full_name, " +
                "CASE WHEN ft.person_id IS NOT NULL THEN 'Full-Time' " +
                "     WHEN cd.person_id IS NOT NULL THEN 'Contract' ELSE 'Driver' END AS dtype " +
                "FROM Person p LEFT JOIN Full_Time_Driver ft ON p.person_id=ft.person_id " +
                "LEFT JOIN Contract_Driver cd ON p.person_id=cd.person_id WHERE p.person_id=?");
            ps.setInt(1, DBConnection.currentPersonId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) { driverName = rs.getString("full_name"); driverType = rs.getString("dtype"); }
            rs.close(); ps.close();
        } catch (Exception ignored) { driverName = DBConnection.currentUser; driverType = "Driver"; }
    }

    void loadDeliveries(String filter) {
        String where = switch (filter) {
            case "ACTIVE" -> "AND d.delivery_status <> 'Completed'";
            case "DONE"   -> "AND d.delivery_status = 'Completed'";
            default -> "";
        };
        DBConnection.loadTablePS(delivTable,
            "SELECT d.delivery_id, d.delivery_date, d.origin, d.destination, " +
            "d.delivery_status, da.role AS my_role, da.hours_worked, " +
            "c.client_name, v.registration_number " +
            "FROM Driver_Assignment da " +
            "JOIN Delivery d ON da.delivery_id=d.delivery_id " +
            "JOIN Client   c ON d.client_id=c.client_id " +
            "JOIN Vehicle  v ON d.vehicle_id=v.vehicle_id " +
            "WHERE da.person_id=? " + where + " ORDER BY d.delivery_date DESC",
            DBConnection.currentPersonId);
    }

    void loadStats() {
        try {
            PreparedStatement p1 = DBConnection.conn.prepareStatement(
                "SELECT total_deliveries_by_driver(?) AS t");
            p1.setInt(1, DBConnection.currentPersonId);
            ResultSet r1 = p1.executeQuery();
            if (r1.next()) statValues[0].setText(String.valueOf(r1.getInt("t")));
            r1.close(); p1.close();

            PreparedStatement p2 = DBConnection.conn.prepareStatement(
                "SELECT IFNULL(SUM(hours_worked),0) AS h FROM Driver_Assignment WHERE person_id=?");
            p2.setInt(1, DBConnection.currentPersonId);
            ResultSet r2 = p2.executeQuery();
            if (r2.next()) statValues[1].setText(r2.getString("h"));
            r2.close(); p2.close();

            PreparedStatement p3 = DBConnection.conn.prepareStatement(
                "SELECT COUNT(*) AS a FROM Driver_Assignment da " +
                "JOIN Delivery d ON da.delivery_id=d.delivery_id " +
                "WHERE da.person_id=? AND d.delivery_status<>'Completed'");
            p3.setInt(1, DBConnection.currentPersonId);
            ResultSet r3 = p3.executeQuery();
            if (r3.next()) statValues[2].setText(String.valueOf(r3.getInt("a")));
            r3.close(); p3.close();

            DBConnection.loadTablePS(statsTable,
                "SELECT d.delivery_id, d.delivery_date, d.origin, d.destination, " +
                "d.delivery_status, da.role, da.hours_worked " +
                "FROM Driver_Assignment da JOIN Delivery d ON da.delivery_id=d.delivery_id " +
                "WHERE da.person_id=? ORDER BY d.delivery_date DESC",
                DBConnection.currentPersonId);
        } catch (Exception e) { UIHelper.err("Stats error:\n" + e.getMessage()); }
    }

    JPanel buildReadOnlyProfile() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIHelper.BG);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UIHelper.BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JLabel avatar = new JLabel("USER", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(avatar);
        content.add(Box.createVerticalStrut(8));

        JLabel nameL = new JLabel(driverName, SwingConstants.CENTER);
        nameL.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameL.setForeground(UIHelper.GOLD);
        nameL.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(nameL);

        JLabel typeL = new JLabel(driverType + " Driver", SwingConstants.CENTER);
        typeL.setFont(UIHelper.BODY);
        typeL.setForeground(UIHelper.ACCENT);
        typeL.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(typeL);
        content.add(Box.createVerticalStrut(6));

        JLabel notice = new JLabel("Employment details are managed by admin", SwingConstants.CENTER);
        notice.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        notice.setForeground(UIHelper.DIM);
        notice.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(notice);
        content.add(Box.createVerticalStrut(16));

        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(UIHelper.CARD);
        infoSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIHelper.GOLD, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        infoSection.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            PreparedStatement ps = DBConnection.conn.prepareStatement(
                "SELECT p.full_name, p.address, p.phone, p.date_of_birth, " +
                "COALESCE(ft.employee_number, cd.contract_number) AS ref_no, " +
                "ft.salary, ft.hire_date, cd.hourly_rate " +
                "FROM Person p " +
                "LEFT JOIN Full_Time_Driver ft ON p.person_id=ft.person_id " +
                "LEFT JOIN Contract_Driver  cd ON p.person_id=cd.person_id " +
                "WHERE p.person_id=?");
            ps.setInt(1, DBConnection.currentPersonId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String payStr = driverType.equals("Full-Time")
                    ? "M" + rs.getString("salary")
                    : "M" + rs.getString("hourly_rate") + "/hr";
                String[][] infoRows = {
                    {"Person ID",    String.valueOf(DBConnection.currentPersonId)},
                    {"Ref No.",      rs.getString("ref_no") != null ? rs.getString("ref_no") : ""},
                    {"Address",      rs.getString("address") != null ? rs.getString("address") : ""},
                    {"Phone",        rs.getString("phone") != null ? rs.getString("phone") : ""},
                    {"Date of Birth",rs.getString("date_of_birth") != null ? rs.getString("date_of_birth") : ""},
                    {"Pay",          payStr},
                    {"Hire Date",    rs.getString("hire_date") != null ? rs.getString("hire_date") : ""}
                };
                for (String[] r : infoRows) {
                    JPanel rowP = new JPanel(new BorderLayout(20, 0));
                    rowP.setBackground(UIHelper.ROW2);
                    rowP.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
                    rowP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                    JLabel k = new JLabel(r[0]); k.setForeground(UIHelper.DIM); k.setFont(UIHelper.LBL);
                    JLabel v = new JLabel(r[1]); v.setForeground(UIHelper.TEXT); v.setFont(UIHelper.BODY);
                    rowP.add(k, BorderLayout.WEST);
                    rowP.add(v, BorderLayout.EAST);
                    infoSection.add(rowP);
                    infoSection.add(Box.createVerticalStrut(3));
                }
            } else {
                JLabel noData = new JLabel("No profile data found.");
                noData.setForeground(UIHelper.DIM); noData.setFont(UIHelper.BODY);
                infoSection.add(noData);
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            JLabel errL = new JLabel("Error loading profile: " + e.getMessage());
            errL.setForeground(UIHelper.RED); errL.setFont(UIHelper.BODY);
            infoSection.add(errL);
        }

        content.add(infoSection);
        content.add(Box.createVerticalStrut(24));

        JPanel editSection = new JPanel();
        editSection.setLayout(new BoxLayout(editSection, BoxLayout.Y_AXIS));
        editSection.setBackground(UIHelper.CARD);
        editSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, UIHelper.ACCENT),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        editSection.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel editTitle = new JLabel("Update My Contact Details");
        editTitle.setFont(UIHelper.HEAD); editTitle.setForeground(UIHelper.ACCENT);
        editTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        editSection.add(editTitle);
        editSection.add(Box.createVerticalStrut(10));

        JTextField pPhone   = UIHelper.field(20);
        JTextField pAddress = UIHelper.field(20);
        pPhone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pPhone.setAlignmentX(Component.LEFT_ALIGNMENT);
        pAddress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pAddress.setAlignmentX(Component.LEFT_ALIGNMENT);

        try {
            PreparedStatement ps = DBConnection.conn.prepareStatement(
                "SELECT phone, address FROM Person WHERE person_id=?");
            ps.setInt(1, DBConnection.currentPersonId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                pPhone.setText(rs.getString("phone") != null ? rs.getString("phone") : "");
                pAddress.setText(rs.getString("address") != null ? rs.getString("address") : "");
            }
            rs.close(); ps.close();
        } catch (Exception ignored) {}

        UIHelper.row(editSection, "Phone",   pPhone);
        UIHelper.row(editSection, "Address", pAddress);

        JButton btnSave = UIHelper.btn("Save Contact Details", UIHelper.ACCENT);
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        editSection.add(Box.createVerticalStrut(12));
        editSection.add(btnSave);

        btnSave.addActionListener(e -> {
            try {
                PreparedStatement ps = DBConnection.conn.prepareStatement(
                    "UPDATE Person SET phone=?, address=? WHERE person_id=?");
                ps.setString(1, pPhone.getText().trim());
                ps.setString(2, pAddress.getText().trim());
                ps.setInt(3, DBConnection.currentPersonId);
                ps.executeUpdate(); ps.close();
                UIHelper.info("Contact details saved successfully!");
            } catch (Exception ex) { UIHelper.err("Save error:\n" + ex.getMessage()); }
        });

        content.add(editSection);
        content.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(content);
        scroll.getViewport().setBackground(UIHelper.BG);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scroll, BorderLayout.CENTER);
        return root;
    }
}
