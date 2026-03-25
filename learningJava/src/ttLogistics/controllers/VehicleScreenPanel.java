package ttLogistics.controllers;

import ttLogistics.AppFrame;
import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class VehicleScreenPanel extends JPanel {

    DefaultTableModel model = new DefaultTableModel();
    JTable table;
    AppFrame appFrame;

    public VehicleScreenPanel(AppFrame appFrame) {
        this.appFrame = appFrame;
        setBackground(UIHelper.BG);
        setLayout(new BorderLayout());
        add(UIHelper.hdr("VM", "Vehicle Management"), BorderLayout.NORTH);
        table = UIHelper.styledTable(model);

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(UIHelper.BG);
        side.setPreferredSize(new Dimension(290, 0));

        JPanel addCard = UIHelper.sideCard("Add Vehicle", UIHelper.ACCENT);
        JTextField fReg = UIHelper.field(16, "TR-001"); JTextField fType = UIHelper.field(16, "e.g., Box Truck"); JTextField fCap = UIHelper.field(16, "5000");
        JTextField fDate = UIHelper.field(16, "2026-01-15"); 
        JComboBox<String> fDepot = UIHelper.dropdownFromMap(DBConnection.getDepots());
        UIHelper.row(addCard, "Registration No.", fReg);
        UIHelper.row(addCard, "Vehicle Type",     fType);
        UIHelper.row(addCard, "Capacity (kg)",    fCap);
        UIHelper.row(addCard, "Purchase Date",    fDate);
        UIHelper.row(addCard, "Depot",            fDepot);
        JButton btnAdd = UIHelper.btn("Add Vehicle", UIHelper.ACCENT);
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        addCard.add(Box.createVerticalStrut(10));
        addCard.add(btnAdd);

        JPanel mCard = UIHelper.sideCard("Record Maintenance", UIHelper.GOLD);
        JComboBox<String> mVid = UIHelper.dropdownFromMap(DBConnection.getVehicles()); 
        JTextField mDate = UIHelper.field(16, "2026-03-25");
        JTextField mType = UIHelper.field(16, "e.g., Oil Change"); JTextField mCost = UIHelper.field(16, "500");
        UIHelper.row(mCard, "Vehicle ID", mVid);
        UIHelper.row(mCard, "Date",       mDate);
        UIHelper.row(mCard, "Type",       mType);
        UIHelper.row(mCard, "Cost (M)",   mCost);
        JButton btnM = UIHelper.btn("Record Maintenance", UIHelper.GOLD);
        btnM.setAlignmentX(Component.LEFT_ALIGNMENT);
        mCard.add(Box.createVerticalStrut(10));
        mCard.add(btnM);

        side.add(addCard);
        side.add(Box.createVerticalStrut(10));
        side.add(mCard);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, UIHelper.scrollFor(table), side);
        split.setDividerLocation(710);
        split.setBackground(UIHelper.BG);
        add(split, BorderLayout.CENTER);

        JButton btnLoad    = UIHelper.btn("Load Vehicles", UIHelper.ACCENT);
        JButton btnDel     = UIHelper.btn("Delete Selected", UIHelper.RED);
        JButton btnMV      = UIHelper.btn("View Maintenance", UIHelper.GOLD);
        JButton btnRefresh = UIHelper.btn("Refresh", new Color(0, 160, 160));
        JButton btnBack    = UIHelper.btn("← Back to Menu", new Color(120, 120, 120));
        btnBack.addActionListener(e -> appFrame.switchToMenu());
        add(UIHelper.bottomBar(null, btnBack, btnLoad, btnDel, btnMV, btnRefresh), BorderLayout.SOUTH);

        final String[] lastView = {"VEH"};

        btnLoad.addActionListener(e -> { lastView[0] = "VEH"; DBConnection.loadTable(table,
            "SELECT v.vehicle_id,v.registration_number,v.vehicle_type,v.capacity,v.purchase_date," +
            "IFNULL(d.depot_name,'') AS depot FROM Vehicle v LEFT JOIN Depot d ON v.depot_id=d.depot_id"); });

        btnMV.addActionListener(e -> { lastView[0] = "MAINT"; DBConnection.loadTable(table,
            "SELECT m.maintenance_id,v.registration_number,m.maintenance_date,m.maintenance_type,m.cost " +
            "FROM Vehicle_Maintenance m JOIN Vehicle v ON m.vehicle_id=v.vehicle_id " +
            "ORDER BY m.maintenance_date DESC"); });

        btnRefresh.addActionListener(e -> {
            if ("MAINT".equals(lastView[0])) btnMV.doClick(); else btnLoad.doClick();
        });

        btnAdd.addActionListener(e -> {
            try {
                PreparedStatement ps = DBConnection.conn.prepareStatement(
                    "INSERT INTO Vehicle(registration_number,vehicle_type,capacity,purchase_date,depot_id) VALUES(?,?,?,?,?)");
                ps.setString(1, fReg.getText().trim());
                ps.setString(2, fType.getText().trim());
                ps.setDouble(3, Double.parseDouble(fCap.getText().trim()));
                ps.setString(4, fDate.getText().trim());
                ps.setInt(5, UIHelper.getSelectedId(fDepot));
                ps.executeUpdate(); ps.close();
                UIHelper.info("Vehicle added!");
                btnLoad.doClick();
            } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
        });

        btnM.addActionListener(e -> {
            try {
                DBConnection.callProc("{CALL record_vehicle_maintenance(?,?,?,?)}",
                    UIHelper.getSelectedId(mVid),
                    mDate.getText().trim(),
                    mType.getText().trim(),
                    Double.parseDouble(mCost.getText().trim()));
                btnMV.doClick();
            } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
        });

        btnDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { UIHelper.err("Select a vehicle."); return; }
            Object id = model.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(VehicleScreenPanel.this, "Delete vehicle #" + id + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement ps = DBConnection.conn.prepareStatement(
                        "DELETE FROM Vehicle WHERE vehicle_id=?");
                    ps.setInt(1, (int) id); ps.executeUpdate(); ps.close();
                    btnLoad.doClick();
                } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
            }
        });

        btnLoad.doClick();
    }
}
