package ttLogistics.controllers;

import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;

public class ReportsScreen extends JFrame {

    DefaultTableModel model = new DefaultTableModel();
    JTable table;

    public ReportsScreen() {
        setTitle("Reports");
        setSize(960, 580);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG);
        setLayout(new java.awt.BorderLayout());
        add(UIHelper.hdr("RP", "Reports & Analytics"), java.awt.BorderLayout.NORTH);
        table = UIHelper.styledTable(model);
        add(UIHelper.scrollFor(table), java.awt.BorderLayout.CENTER);

        JButton b1       = UIHelper.btn("Active Deliveries", UIHelper.ACCENT);
        JButton b2       = UIHelper.btn("Driver Workload", UIHelper.BLUE);
        JButton b3       = UIHelper.btn("Maintenance Costs", UIHelper.GOLD);
        JButton b4       = UIHelper.btn("Delivery Log", new java.awt.Color(160, 90, 220));
        JButton b5       = UIHelper.btn("All Deliveries", UIHelper.DIM);
        JButton bRefresh = UIHelper.btn("Refresh", new java.awt.Color(0, 160, 160));
        JTextField pidF  = UIHelper.field(6, "ID");
        JButton bF       = UIHelper.btn("Driver Total", new java.awt.Color(220, 130, 50));
        JButton btnBack  = UIHelper.btn("← Back to Menu", new java.awt.Color(120, 120, 120));

        final Runnable[] lastAction = {null};

        btnBack.addActionListener(e -> { dispose(); new MainMenu(); });
        add(UIHelper.bottomBar(this, btnBack, b1, b2, b3, b4, b5, UIHelper.lbl("  Person ID:"), pidF, bF, bRefresh),
            java.awt.BorderLayout.SOUTH);

        b1.addActionListener(e -> { lastAction[0] = () -> DBConnection.loadTable(table,
            "SELECT * FROM Active_Deliveries_View"); lastAction[0].run(); });

        b2.addActionListener(e -> { lastAction[0] = () -> DBConnection.loadTable(table,
            "SELECT * FROM Driver_Workload_View ORDER BY total_hours_worked DESC"); lastAction[0].run(); });

        b3.addActionListener(e -> { lastAction[0] = () -> DBConnection.loadTable(table,
            "SELECT v.vehicle_id,v.registration_number,vehicle_total_maintenance(v.vehicle_id) AS total_maintenance_cost " +
            "FROM Vehicle v ORDER BY total_maintenance_cost DESC"); lastAction[0].run(); });

        b4.addActionListener(e -> { lastAction[0] = () -> DBConnection.loadTable(table,
            "SELECT log_id,delivery_id,log_date,description FROM Delivery_Log ORDER BY log_date DESC");
            lastAction[0].run(); });

        b5.addActionListener(e -> { lastAction[0] = () -> DBConnection.loadTable(table,
            "SELECT d.delivery_id,d.delivery_date,d.origin,d.destination,d.delivery_status," +
            "c.client_name,v.registration_number FROM Delivery d " +
            "JOIN Client c ON d.client_id=c.client_id " +
            "JOIN Vehicle v ON d.vehicle_id=v.vehicle_id ORDER BY d.delivery_date DESC");
            lastAction[0].run(); });

        bRefresh.addActionListener(e -> { if (lastAction[0] != null) lastAction[0].run(); else b1.doClick(); });

        bF.addActionListener(e -> {
            String pid = pidF.getText().trim();
            if (pid.isEmpty()) { UIHelper.err("Enter Person ID."); return; }
            lastAction[0] = () -> {
                try {
                    PreparedStatement ps = DBConnection.conn.prepareStatement(
                        "SELECT p.full_name,total_deliveries_by_driver(?) AS total FROM Person p WHERE p.person_id=?");
                    ps.setInt(1, Integer.parseInt(pid));
                    ps.setInt(2, Integer.parseInt(pid));
                    ResultSet rs = ps.executeQuery();
                    DefaultTableModel m = (DefaultTableModel) table.getModel();
                    m.setRowCount(0);
                    m.setColumnIdentifiers(new String[]{"Driver Name", "Total Deliveries"});
                    if (rs.next()) m.addRow(new Object[]{rs.getString(1), rs.getInt(2)});
                    else UIHelper.err("No person #" + pid);
                    rs.close(); ps.close();
                } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
            };
            lastAction[0].run();
        });

        b1.doClick();
        setVisible(true);
    }
}
