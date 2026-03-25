package ttLogistics.controllers;

import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ClientScreen extends JFrame {

    DefaultTableModel model = new DefaultTableModel();
    JTable table;
    JTextField fName, fPhone, fAddr;

    public ClientScreen() {
        setTitle("Client Management");
        setSize(820, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG);
        setLayout(new BorderLayout());
        add(UIHelper.hdr("CL", "Client Management"), BorderLayout.NORTH);
        table = UIHelper.styledTable(model);

        JPanel side = UIHelper.sideCard("Add Client", UIHelper.ACCENT);
        side.setPreferredSize(new Dimension(270, 0));
        fName = UIHelper.field(16, "e.g., John Doe"); fPhone = UIHelper.field(16, "e.g., 670-123-4567"); fAddr = UIHelper.field(16, "e.g., 123 Main St");
        UIHelper.row(side, "Client Name", fName);
        UIHelper.row(side, "Phone",       fPhone);
        UIHelper.row(side, "Address",     fAddr);
        JButton btnAdd = UIHelper.btn("Add Client", UIHelper.ACCENT);
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(Box.createVerticalStrut(12));
        side.add(btnAdd);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, UIHelper.scrollFor(table), side);
        split.setDividerLocation(530);
        split.setBackground(UIHelper.BG);
        add(split, BorderLayout.CENTER);

        JButton btnLoad    = UIHelper.btn("Load Clients", UIHelper.ACCENT);
        JButton btnDel     = UIHelper.btn("Delete Selected", UIHelper.RED);
        JButton btnRefresh = UIHelper.btn("Refresh", new Color(0, 160, 160));
        JButton btnBack    = UIHelper.btn("← Back to Menu", new Color(120, 120, 120));
        btnBack.addActionListener(e -> { dispose(); new MainMenu(); });
        add(UIHelper.bottomBar(this, btnBack, btnLoad, btnDel, btnRefresh), BorderLayout.SOUTH);

        btnLoad.addActionListener(e -> DBConnection.loadTable(table,
            "SELECT client_id,client_name,contact_phone,address FROM Client ORDER BY client_name"));

        btnRefresh.addActionListener(e -> btnLoad.doClick());

        btnAdd.addActionListener(e -> {
            try {
                PreparedStatement ps = DBConnection.conn.prepareStatement(
                    "INSERT INTO Client(client_name,contact_phone,address) VALUES(?,?,?)");
                ps.setString(1, fName.getText().trim());
                ps.setString(2, fPhone.getText().trim());
                ps.setString(3, fAddr.getText().trim());
                ps.executeUpdate(); ps.close();
                UIHelper.info("Client added!");
                btnLoad.doClick();
            } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
        });

        btnDel.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { UIHelper.err("Select a client."); return; }
            Object cid = model.getValueAt(r, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete client #" + cid + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    PreparedStatement ps = DBConnection.conn.prepareStatement(
                        "DELETE FROM Client WHERE client_id=?");
                    ps.setInt(1, (int) cid); ps.executeUpdate(); ps.close();
                    btnLoad.doClick();
                } catch (Exception ex) { UIHelper.err(ex.getMessage()); }
            }
        });

        btnLoad.doClick();
        setVisible(true);
    }
}
