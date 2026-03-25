package ttLogistics;

import ttLogistics.controllers.*;
import ttLogistics.db.DBConnection;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public AppFrame() {
        setTitle("TT Logistics Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(13, 18, 28));

        setContentPane(contentPanel);
        
        // Show login screen first
        LoginScreenPanel loginPanel = new LoginScreenPanel(this);
        contentPanel.add(loginPanel, "login");
        cardLayout.show(contentPanel, "login");
        
        setVisible(true);
    }

    public void showScreen(String name, JPanel screen) {
        contentPanel.add(screen, name);
        cardLayout.show(contentPanel, name);
    }

    public void switchToMenu() {
        showScreen("menu", new MainMenuPanel(this));
    }

    public void switchToVehicles() {
        showScreen("vehicles", new VehicleScreenPanel(this));
    }

    public void switchToDrivers() {
        showScreen("drivers", new DriverScreenPanel(this));
    }

    public void switchToTrips() {
        showScreen("trips", new TripScreenPanel(this));
    }

    public void switchToReports() {
        showScreen("reports", new ReportsScreenPanel(this));
    }

    public void switchToClients() {
        showScreen("clients", new ClientScreenPanel(this));
    }

    public void logout() {
        DBConnection.closeConnection();
        showScreen("login", new LoginScreenPanel(this));
    }
}

