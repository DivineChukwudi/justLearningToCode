package ttLogistics.controllers;

import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("TT Logistics Main Menu");
        setSize(920, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG);
        setLayout(new BorderLayout());

        JPanel topHdr = new JPanel(new BorderLayout());
        topHdr.setBackground(UIHelper.PANEL);
        topHdr.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel lt = new JLabel("TT Logistics Management System");
        lt.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lt.setForeground(UIHelper.ACCENT);
        JLabel lu = new JLabel(DBConnection.currentUser + "  [" + DBConnection.currentRole + "]  ");
        lu.setFont(UIHelper.BODY);
        lu.setForeground(DBConnection.currentRole.equals("admin") ? UIHelper.RED : UIHelper.BLUE);
        topHdr.add(lt, BorderLayout.WEST);
        topHdr.add(lu, BorderLayout.EAST);
        add(topHdr, BorderLayout.NORTH);

        String[][] tiles = {
            {"VM", "Vehicle Management", "Add vehicles & record maintenance"},
            {"DM", "Driver Management",  "Add, edit, delete drivers"},
            {"TM", "Trip Management",    "Create and track deliveries"},
            {"RP", "Reports",            "Views, stats, logs & functions"},
            {"CL", "Clients",            "Manage client records"},
            {"LO", "Logout",             "Return to login screen"}
        };
        Color[] cols = {UIHelper.ACCENT, UIHelper.BLUE, UIHelper.GOLD,
                        new Color(160, 90, 220), new Color(220, 130, 50), UIHelper.RED};

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setBackground(UIHelper.BG);
        grid.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        for (int i = 0; i < tiles.length; i++) {
            final int idx = i;
            JPanel tile = new JPanel(new BorderLayout(0, 6));
            tile.setBackground(UIHelper.CARD);
            tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(cols[i], 1),
                BorderFactory.createEmptyBorder(22, 20, 18, 20)));
            tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JLabel icon = new JLabel(tiles[i][0], SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
            JLabel name = new JLabel(tiles[i][1], SwingConstants.CENTER);
            name.setFont(new Font("Segoe UI", Font.BOLD, 13));
            name.setForeground(cols[i]);
            JLabel desc = new JLabel("<html><center>" + tiles[i][2] + "</center></html>", SwingConstants.CENTER);
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            desc.setForeground(UIHelper.DIM);
            tile.add(icon, BorderLayout.CENTER);
            tile.add(name, BorderLayout.SOUTH);
            tile.add(desc, BorderLayout.NORTH);
            tile.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    switch (idx) {
                        case 0 -> { dispose(); new VehicleScreen(); }
                        case 1 -> { dispose(); new DriverScreen(); }
                        case 2 -> { dispose(); new TripScreen(); }
                        case 3 -> { dispose(); new ReportsScreen(); }
                        case 4 -> { dispose(); new ClientScreen(); }
                        case 5 -> UIHelper.doLogout(MainMenu.this);
                    }
                }
                public void mouseEntered(MouseEvent e) { tile.setBackground(new Color(40, 52, 72)); }
                public void mouseExited(MouseEvent e)  { tile.setBackground(UIHelper.CARD); }
            });
            grid.add(tile);
        }
        add(grid, BorderLayout.CENTER);

        JLabel status = new JLabel("  Connected  •  ttlogistics  •  MySQL 8.0");
        status.setFont(UIHelper.BODY);
        status.setForeground(UIHelper.ACCENT);
        status.setBackground(UIHelper.PANEL);
        status.setOpaque(true);
        status.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        add(status, BorderLayout.SOUTH);
        setVisible(true);
    }
}
