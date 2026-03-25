package ttLogistics.controllers;

import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class TripScreen extends JFrame {

    DefaultTableModel model = new DefaultTableModel();
    JTable table;
    JTextField fDate, fOrigin, fDest, fClientId, fVehicleId;
    JComboBox<String> fStatus;
    JTextField uId;
    JComboBox<String> uStatus;

    public TripScreen() {
        setTitle("Trip Management");
        setSize(1020, 660);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG);
        setLayout(new BorderLayout());
        add(UIHelper.hdr("TM", "Trip / Delivery Management"), BorderLayout.NORTH);
        table = UIHelper.styledTable(model);

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(UIHelper.BG);
        side.setPreferredSize(new Dimension(290, 0));

        JPanel newCard = UIHelper.sideCard("New Delivery", UIHelper.ACCENT);
        fDate = UIHelper.field(16, "2026-03-25"); fOrigin = UIHelper.field(16, "e.g., Warehouse A"); fDest = UIHelper.field(16, "e.g., Store B");
        fClientId = UIHelper.field(16, "Client ID"); fVehicleId = UIHelper.field(16, "Vehicle ID");
        fStatus = UIHelper.combo("Pending", "In Transit", "Completed");
        UIHelper.row(newCard, "Date (YYYY-MM-DD)", fDate);
        UIHelper.row(newCard, "Origin",            fOrigin);
        UIHelper.row(newCard, "Destination",       fDest);
        UIHelper.row(newCard, "Client ID",         fClientId);
        UIHelper.row(newCard, "Vehicle ID",        fVehicleId);
        UIHelper.row(newCard, "Status",            fStatus);
        JButton btnCreate = UIHelper.btn("Create Delivery", UIHelper.ACCENT);
        btnCreate.setAlignmentX(Component.LEFT_ALIGNMENT);
        newCard.add(Box.createVerticalStrut(10));
        newCard.add(btnCreate);

        JPanel updCard = UIHelper.sideCard("Update Status", UIHelper.GOLD);
        uId = UIHelper.field(16, "Delivery ID");
        uStatus = UIHelper.combo("Pending", "In Transit", "Completed");
        UIHelper.row(updCard, "Delivery ID", uId);
        UIHelper.row(updCard, "New Status",  uStatus);
        JButton btnUpd = UIHelper.btn("Update Status", UIHelper.GOLD);
        btnUpd.setAlignmentX(Component.LEFT_ALIGNMENT);
        updCard.add(Box.createVerticalStrut(10));
        updCard.add(btnUpd);

        side.add(newCard);
        side.add(Box.createVerticalStrut(10));
        side.add(updCard);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, UIHelper.scrollFor(table), side);
        split.setDividerLocation(710);
        split.setBackground(UIHelper.BG);
        add(split, BorderLayout.CENTER);

        JButton btnAll     = UIHelper.btn("All Deliveries", UIHelper.ACCENT);
        JButton btnAct     = UIHelper.btn("Active (View)", UIHelper.BLUE);
        JButton btnDel     = UIHelper.btn("Delete Selected", UIHelper.RED);
        JButton btnRefresh = UIHelper.btn("Refresh", new Color(0, 160, 160));
        JButton btnBack    = UIHelper.btn("← Back to Menu", new Color(120, 120, 120));
        btnBack.addActionListener(e -> { dispose(); new MainMenu(); });
        add(UIHelper.bottomBar(this, btnBack, btnAll, btnAct, btnDel, btnRefresh), BorderLayout.SOUTH);

        final String[] lastView = {"ALL"};

        btnAll.addActionListener(e -> { lastView[0] = "ALL"; DBConnection.loadTable(table,
            "SELECT d.delivery_id,d.delivery_date,d.origin,d.destination,d.delivery_status," +
            "c.client_name,v.registration_number FROM Delivery d " +
            "JOIN Client c ON d.client_id=c.client_id " +
            "JOIN Vehicle v ON d.vehicle_id=v.vehicle_id ORDER BY d.delivery_date DESC"); });

        btnAct.addActionListener(e -> { lastView[0] = "ACT";
            DBConnection.loadTable(table, "SELECT * FROM Active_Deliveries_View"); });

        btnRefresh.addActionListener(e -> {
            if ("ACT".equals(lastView[0])) btnAct.doClick(); else btnAll.doClick();
        });

        btnCreate.addActionListener(e -> {
            try {
                PreparedStatement ps = DBConnection.conn.prepareStatement(
                    "INSERT INTO Delivery(delivery_date,origin,destination,delivery_status,client_id,vehicle_id) VALUES(?,?,?,?,?,?)");
                ps.setString(1, fDate.getText().trim());
                ps.setString(2, fOrigin.getText().trim());
                ps.setString(3, fDest.getText().trim());
                ps.setString(4, (String) fStatus.getSelectedItem());
                ps.setInt(5, Integer.parseInt(fClientId.getText().trim()));
                ps.setInt(6, Integer.parseInt(fVehicleId.getText().trim()));
                ps.executeUpdate(); ps.close();
                UIHelper.info("Delivery created!");
                btnAll.doClick();
            } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
        });

        btnUpd.addActionListener(e -> {
            try {
                PreparedStatement ps = DBConnection.conn.prepareStatement(
                    "UPDATE Delivery SET delivery_status=? WHERE delivery_id=?");
                ps.setString(1, (String) uStatus.getSelectedItem());
                ps.setInt(2, Integer.parseInt(uId.getText().trim()));
                int rows = ps.executeUpdate(); ps.close();
                UIHelper.info(rows > 0 ? "Status updated!" : "Not found.");
                btnAll.doClick();
            } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
        });

        btnDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { UIHelper.err("Select a delivery."); return; }
            Object did = model.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete delivery #" + did + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement ps = DBConnection.conn.prepareStatement(
                        "DELETE FROM Delivery WHERE delivery_id=?");
                    ps.setInt(1, (int) did); ps.executeUpdate(); ps.close();
                    btnAll.doClick();
                } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
            }
        });

        btnAll.doClick();
        setVisible(true);
    }
}
