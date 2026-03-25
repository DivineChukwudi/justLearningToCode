package ttLogistics.controllers;

import ttLogistics.AppFrame;
import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class DriverScreenPanel extends JPanel {

    DefaultTableModel model = new DefaultTableModel();
    JTable table;
    AppFrame appFrame;

    public DriverScreenPanel(AppFrame appFrame) {
        this.appFrame = appFrame;
        setBackground(UIHelper.BG);
        setLayout(new BorderLayout());
        add(UIHelper.hdr("DM", "Driver Management"), BorderLayout.NORTH);
        table = UIHelper.styledTable(model);

        JPanel sideInner = new JPanel();
        sideInner.setLayout(new BoxLayout(sideInner, BoxLayout.Y_AXIS));
        sideInner.setBackground(UIHelper.BG);

        JPanel addCard = UIHelper.sideCard("Add New Driver", UIHelper.ACCENT);
        JTextField fName = UIHelper.field(18, "e.g., John Smith"); JTextField fAddr = UIHelper.field(18, "e.g., 123 Oak Ave");
        JTextField fPhone = UIHelper.field(18, "e.g., 670-555-0123"); JTextField fDob = UIHelper.field(18, "YYYY-MM-DD");
        UIHelper.row(addCard, "Full Name",     fName);
        UIHelper.row(addCard, "Address",       fAddr);
        UIHelper.row(addCard, "Phone",         fPhone);
        UIHelper.row(addCard, "Date of Birth", fDob);
        JComboBox<String> driverType = UIHelper.combo("Full-Time", "Contract");
        UIHelper.row(addCard, "Driver Type",   driverType);
        JTextField fEmpNo = UIHelper.field(18, "e.g., E001"); JTextField fSalary = UIHelper.field(18, "e.g., 25000"); JTextField fHire = UIHelper.field(18, "YYYY-MM-DD");
        UIHelper.row(addCard, "Employee No.", fEmpNo);
        UIHelper.row(addCard, "Salary (M)",   fSalary);
        UIHelper.row(addCard, "Hire Date",    fHire);
        JTextField fConNo = UIHelper.field(18, "e.g., CON-001"); JTextField fRate = UIHelper.field(18, "e.g., 150");
        UIHelper.row(addCard, "Contract No.", fConNo);
        UIHelper.row(addCard, "Hourly Rate",  fRate);
        JButton btnAdd = UIHelper.btn("Add Driver + Create Login", UIHelper.ACCENT);
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        addCard.add(Box.createVerticalStrut(12));
        addCard.add(btnAdd);

        JPanel assignCard = UIHelper.sideCard("Assign to Delivery", new Color(160, 90, 220));
        JComboBox<String> aPid = UIHelper.dropdownFromMap(DBConnection.getDrivers()); 
        JComboBox<String> aDid = UIHelper.dropdownFromMap(DBConnection.getDeliveries());
        JTextField aHours = UIHelper.field(18, "Hours worked"); JComboBox<String> aRole = UIHelper.combo("main driver", "assistant");
        UIHelper.row(assignCard, "Driver",       aPid);
        UIHelper.row(assignCard, "Delivery",     aDid);
        UIHelper.row(assignCard, "Role",         aRole);
        UIHelper.row(assignCard, "Hours Worked", aHours);
        JButton btnAssign = UIHelper.btn("Assign Driver", new Color(160, 90, 220));
        btnAssign.setAlignmentX(Component.LEFT_ALIGNMENT);
        assignCard.add(Box.createVerticalStrut(10));
        assignCard.add(btnAssign);

        sideInner.add(addCard);
        sideInner.add(Box.createVerticalStrut(10));
        sideInner.add(assignCard);
        sideInner.add(Box.createVerticalStrut(10));

        JScrollPane sideScroll = new JScrollPane(sideInner);
        sideScroll.getViewport().setBackground(UIHelper.BG);
        sideScroll.setBorder(null);
        sideScroll.setPreferredSize(new Dimension(320, 0));
        sideScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, UIHelper.scrollFor(table), sideScroll);
        split.setDividerLocation(750);
        split.setBackground(UIHelper.BG);
        add(split, BorderLayout.CENTER);

        JButton btnFT     = UIHelper.btn("Full-Time Drivers", UIHelper.ACCENT);
        JButton btnCon    = UIHelper.btn("Contract Drivers", UIHelper.BLUE);
        JButton btnAss    = UIHelper.btn("Assignments", new Color(160, 90, 220));
        JButton btnAll    = UIHelper.btn("All Persons", UIHelper.DIM);
        JButton btnRefresh= UIHelper.btn("Refresh", new Color(0, 160, 160));
        JButton btnDelete = UIHelper.btn("Delete Driver", UIHelper.RED);
        JButton btnBack   = UIHelper.btn("← Back to Menu", new Color(120, 120, 120));
        btnBack.addActionListener(e -> appFrame.switchToMenu());
        add(UIHelper.bottomBar(null, btnBack, btnFT, btnCon, btnAss, btnAll, btnRefresh, btnDelete), BorderLayout.SOUTH);

        final String[] lastQuery = {null};

        btnFT.addActionListener(e -> { lastQuery[0] = "FT"; DBConnection.loadTable(table,
            "SELECT p.person_id,p.full_name,p.phone,f.employee_number,f.salary,f.hire_date " +
            "FROM Person p JOIN Full_Time_Driver f ON p.person_id=f.person_id ORDER BY p.full_name"); });

        btnCon.addActionListener(e -> { lastQuery[0] = "CON"; DBConnection.loadTable(table,
            "SELECT p.person_id,p.full_name,p.phone,d.contract_number,d.hourly_rate " +
            "FROM Person p JOIN Contract_Driver d ON p.person_id=d.person_id ORDER BY p.full_name"); });

        btnAss.addActionListener(e -> { lastQuery[0] = "ASS"; DBConnection.loadTable(table,
            "SELECT da.person_id,p.full_name,d.delivery_id,da.role,da.hours_worked " +
            "FROM Driver_Assignment da JOIN Person p ON da.person_id=p.person_id " +
            "JOIN Delivery d ON da.delivery_id=d.delivery_id"); });

        btnAll.addActionListener(e -> { lastQuery[0] = "ALL"; DBConnection.loadTable(table,
            "SELECT person_id,full_name,phone,date_of_birth,address FROM Person " +
            "WHERE person_id IN (SELECT person_id FROM Full_Time_Driver UNION SELECT person_id FROM Contract_Driver) " +
            "ORDER BY full_name"); });

        btnRefresh.addActionListener(e -> {
            if ("FT".equals(lastQuery[0])) btnFT.doClick();
            else if ("CON".equals(lastQuery[0])) btnCon.doClick();
            else if ("ASS".equals(lastQuery[0])) btnAss.doClick();
            else btnAll.doClick();
        });

        btnAdd.addActionListener(e -> {
            try {
                String name = fName.getText().trim();
                String dob = fDob.getText().trim();
                String phone = fPhone.getText().trim();
                String addr = fAddr.getText().trim();
                String type = (String) driverType.getSelectedItem();

                DBConnection.conn.setAutoCommit(false);
                
                PreparedStatement ps1 = DBConnection.conn.prepareStatement(
                    "INSERT INTO Person(full_name,date_of_birth,phone,address) VALUES(?,?,?,?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS);
                ps1.setString(1, name); ps1.setString(2, dob); ps1.setString(3, phone); ps1.setString(4, addr);
                ps1.executeUpdate();
                ResultSet rs = ps1.getGeneratedKeys();
                int newPid = rs.next() ? rs.getInt(1) : -1;
                rs.close(); ps1.close();

                if ("Full-Time".equals(type)) {
                    PreparedStatement ps2 = DBConnection.conn.prepareStatement(
                        "INSERT INTO Full_Time_Driver(person_id,employee_number,salary,hire_date) VALUES(?,?,?,?)");
                    ps2.setInt(1, newPid);
                    ps2.setString(2, fEmpNo.getText().trim());
                    ps2.setDouble(3, Double.parseDouble(fSalary.getText().trim()));
                    ps2.setString(4, fHire.getText().trim());
                    ps2.executeUpdate(); ps2.close();
                } else {
                    PreparedStatement ps2 = DBConnection.conn.prepareStatement(
                        "INSERT INTO Contract_Driver(person_id,contract_number,hourly_rate) VALUES(?,?,?)");
                    ps2.setInt(1, newPid);
                    ps2.setString(2, fConNo.getText().trim());
                    ps2.setDouble(3, Double.parseDouble(fRate.getText().trim()));
                    ps2.executeUpdate(); ps2.close();
                }

                String username = name.toLowerCase().replace(" ", "_") + "_" + newPid;
                String password = "Driver@" + newPid;
                PreparedStatement ps3 = DBConnection.conn.prepareStatement(
                    "INSERT INTO App_Users(username,password_hash,role,person_id) VALUES(?,SHA2(?,256),?,?)");
                ps3.setString(1, username); ps3.setString(2, password); ps3.setString(3, "driver"); ps3.setInt(4, newPid);
                ps3.executeUpdate(); ps3.close();

                DBConnection.conn.commit(); DBConnection.conn.setAutoCommit(true);
                UIHelper.info("Driver #" + newPid + " created!\nUsername: " + username + "\nPassword: " + password);
                btnAll.doClick();
            } catch (Exception ex) {
                try { DBConnection.conn.rollback(); DBConnection.conn.setAutoCommit(true); } catch (Exception ignored) {}
                UIHelper.err("Add error:\n" + ex.getMessage());
            }
        });

        btnAssign.addActionListener(e -> {
            try {
                DBConnection.callProc("{CALL assign_driver_to_delivery(?,?,?,?)}",
                    UIHelper.getSelectedId(aPid),
                    UIHelper.getSelectedId(aDid),
                    (String) aRole.getSelectedItem(),
                    Double.parseDouble(aHours.getText().trim()));
                btnAss.doClick();
            } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow(); if (r < 0) { UIHelper.err("Select a driver to delete."); return; }
            int pid = (int) model.getValueAt(r, 0);
            String name = model.getValueAt(r, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(DriverScreenPanel.this,
                "Delete driver: " + name + " (ID: " + pid + ")?\n\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    DBConnection.conn.setAutoCommit(false);
                    PreparedStatement ps1 = DBConnection.conn.prepareStatement(
                        "DELETE FROM App_Users WHERE person_id=?");
                    ps1.setInt(1, pid); ps1.executeUpdate(); ps1.close();
                    PreparedStatement ps2 = DBConnection.conn.prepareStatement(
                        "DELETE FROM Person WHERE person_id=?");
                    ps2.setInt(1, pid); ps2.executeUpdate(); ps2.close();
                    DBConnection.conn.commit(); DBConnection.conn.setAutoCommit(true);
                    UIHelper.info("Driver " + name + " deleted successfully.");
                    btnAll.doClick();
                } catch (Exception ex) {
                    try { DBConnection.conn.rollback(); DBConnection.conn.setAutoCommit(true); } catch (Exception ignored) {}
                    UIHelper.err("Delete error:\n" + ex.getMessage());
                }
            }
        });

        btnFT.doClick();
    }
}
