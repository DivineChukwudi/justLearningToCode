package ttLogistics.controllers;

import ttLogistics.AppFrame;
import ttLogistics.db.DBConnection;
import ttLogistics.db.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreenPanel extends JPanel {

    JTextField userF;
    JPasswordField passF;
    AppFrame appFrame;

    public LoginScreenPanel(AppFrame appFrame) {
        this.appFrame = appFrame;
        setBackground(UIHelper.BG);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIHelper.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 190, 130, 80), 1),
            BorderFactory.createEmptyBorder(28, 34, 28, 34)));
        card.setPreferredSize(new Dimension(370, 490));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 0, 5, 0);

        JLabel ico = new JLabel("TRUCK", SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        g.gridy = 0;
        card.add(ico, g);

        JLabel title = new JLabel("TT Logistics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(UIHelper.ACCENT);
        g.gridy = 1;
        card.add(title, g);

        JLabel sub = new JLabel("Database Management System", SwingConstants.CENTER);
        sub.setFont(UIHelper.BODY);
        sub.setForeground(UIHelper.DIM);
        g.gridy = 2;
        g.insets = new Insets(0, 0, 12, 0);
        card.add(sub, g);

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        badges.setBackground(UIHelper.CARD);
        for (String[] b2 : new String[][]{{"Admin", "#00BE82"}, {"Staff", "#3C82DC"}, {"Driver", "#FFB932"}}) {
            JLabel bl = new JLabel(b2[0]);
            bl.setFont(new Font("Segoe UI", Font.BOLD, 10));
            bl.setForeground(Color.decode(b2[1]));
            bl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode(b2[1])),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
            badges.add(bl);
        }
        g.gridy = 3;
        g.insets = new Insets(0, 0, 16, 0);
        card.add(badges, g);
        g.insets = new Insets(4, 0, 4, 0);

        g.gridy = 4;
        card.add(UIHelper.lbl("Username"), g);
        userF = UIHelper.field(20, "Enter username");
        userF.setPreferredSize(new Dimension(300, 36));
        g.gridy = 5;
        card.add(userF, g);

        g.gridy = 6;
        card.add(UIHelper.lbl("Password"), g);
        passF = UIHelper.passField();
        passF.setPreferredSize(new Dimension(300, 36));
        g.gridy = 7;
        card.add(passF, g);

        JButton btnLogin = UIHelper.btn("  LOGIN  ", UIHelper.ACCENT);
        btnLogin.setPreferredSize(new Dimension(300, 42));
        g.gridy = 8;
        g.insets = new Insets(16, 0, 8, 0);
        card.add(btnLogin, g);

        JPanel hints = new JPanel(new GridLayout(3, 1, 0, 2));
        hints.setBackground(new Color(20, 28, 42));
        hints.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        for (String h2 : new String[]{
                "admin_thabo / Admin@1234  [admin]",
                "thabo / Driver@1  •  palesa / Driver@2",
                "kamohelo / Driver@4  •  tsepiso / Driver@5"}) {
            JLabel hl = new JLabel(h2, SwingConstants.CENTER);
            hl.setFont(new Font("Consolas", Font.PLAIN, 10));
            hl.setForeground(UIHelper.DIM);
            hints.add(hl);
        }
        g.gridy = 9;
        g.insets = new Insets(4, 0, 0, 0);
        card.add(hints, g);

        add(card);

        ActionListener doLogin = e -> login();
        btnLogin.addActionListener(doLogin);
        passF.addActionListener(doLogin);
    }

    void login() {
        String user = userF.getText().trim();
        String pass = new String(passF.getPassword());
        if (user.isEmpty() || pass.isEmpty()) { UIHelper.err("Enter username and password."); return; }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection chk = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DRIVER_DBUSER, DBConnection.DRIVER_DBPASS);
            PreparedStatement ps = chk.prepareStatement(
                "SELECT role,person_id FROM App_Users WHERE username=? AND password_hash=SHA2(?,256)");
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) { UIHelper.err("Invalid username or password."); return; }
            String role = rs.getString("role");
            int pid = rs.getObject("person_id") != null ? rs.getInt("person_id") : -1;
            rs.close(); ps.close(); chk.close();
            
            DBConnection.conn = DriverManager.getConnection(DBConnection.DB_URL, DBConnection.DRIVER_DBUSER, DBConnection.DRIVER_DBPASS);
            DBConnection.currentUser = user;
            DBConnection.currentRole = role;
            DBConnection.currentPersonId = pid;
            
            if ("driver".equals(role)) {
                new DriverDashboard();
                appFrame.dispose();
            } else {
                appFrame.switchToMenu();
            }
        } catch (Exception ex) { UIHelper.err("Login error:\n" + ex.getMessage()); }
    }
}
