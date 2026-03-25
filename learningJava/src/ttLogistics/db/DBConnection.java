package ttLogistics.db;

import javax.swing.*;
import javax.swing.table.*;
import java.sql.*;
import java.util.*;

public class DBConnection {

    public static final String DB_URL        = "jdbc:mysql://localhost:3306/ttlogistics?useSSL=false&allowPublicKeyRetrieval=true";
    public static final String DRIVER_DBUSER = "root";
    public static final String DRIVER_DBPASS = "divinea240872";

    public static Connection conn;
    public static String currentUser     = "";
    public static String currentRole     = "";
    public static int    currentPersonId = -1;

    public static void closeConnection() {
        try { if (conn != null && !conn.isClosed()) conn.close(); } catch (Exception ignored) {}
        conn = null;
        currentUser = "";
        currentRole = "";
        currentPersonId = -1;
    }

    public static void loadTable(JTable table, String sql) {
        try {
            DefaultTableModel m = (DefaultTableModel) table.getModel();
            m.setRowCount(0);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            String[] names = new String[cols];
            for (int i = 1; i <= cols; i++) names[i - 1] = meta.getColumnLabel(i);
            m.setColumnIdentifiers(names);
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 1; i <= cols; i++) row[i - 1] = rs.getObject(i);
                m.addRow(row);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            UIHelper.err("Query error:\n" + e.getMessage());
        }
    }

    public static void loadTablePS(JTable table, String sql, Object... params) {
        try {
            DefaultTableModel m = (DefaultTableModel) table.getModel();
            m.setRowCount(0);
            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) ps.setInt(i + 1, (Integer) params[i]);
                else ps.setString(i + 1, params[i].toString());
            }
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            String[] names = new String[cols];
            for (int i = 1; i <= cols; i++) names[i - 1] = meta.getColumnLabel(i);
            m.setColumnIdentifiers(names);
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 1; i <= cols; i++) row[i - 1] = rs.getObject(i);
                m.addRow(row);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            UIHelper.err("Query error:\n" + e.getMessage());
        }
    }

    public static void callProc(String sql, Object... params) {
        try {
            CallableStatement cs = conn.prepareCall(sql);
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) cs.setInt(i + 1, (Integer) params[i]);
                else if (params[i] instanceof Double) cs.setDouble(i + 1, (Double) params[i]);
                else cs.setString(i + 1, params[i].toString());
            }
            cs.execute();
            ResultSet rs = cs.getResultSet();
            if (rs != null && rs.next()) UIHelper.info(rs.getString(1));
            else UIHelper.info("Done.");
            cs.close();
        } catch (SQLException e) {
            UIHelper.err("Procedure error:\n" + e.getMessage());
        }
    }

    // Fetch data for dropdowns - returns Map<ID, Display Name>
    public static Map<Integer, String> getClients() {
        Map<Integer, String> map = new LinkedHashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT client_id, client_name FROM Client ORDER BY client_name");
            while (rs.next()) map.put(rs.getInt(1), rs.getInt(1) + " - " + rs.getString(2));
            rs.close(); st.close();
        } catch (SQLException e) { UIHelper.err("Error fetching clients:\n" + e.getMessage()); }
        return map;
    }

    public static Map<Integer, String> getVehicles() {
        Map<Integer, String> map = new LinkedHashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT vehicle_id, registration_number FROM Vehicle ORDER BY registration_number");
            while (rs.next()) map.put(rs.getInt(1), rs.getInt(1) + " - " + rs.getString(2));
            rs.close(); st.close();
        } catch (SQLException e) { UIHelper.err("Error fetching vehicles:\n" + e.getMessage()); }
        return map;
    }

    public static Map<Integer, String> getDepots() {
        Map<Integer, String> map = new LinkedHashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT depot_id, depot_name FROM Depot ORDER BY depot_name");
            while (rs.next()) map.put(rs.getInt(1), rs.getInt(1) + " - " + rs.getString(2));
            rs.close(); st.close();
        } catch (SQLException e) { UIHelper.err("Error fetching depots:\n" + e.getMessage()); }
        return map;
    }

    public static Map<Integer, String> getDrivers() {
        Map<Integer, String> map = new LinkedHashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT person_id, full_name FROM Person WHERE person_id IN (SELECT person_id FROM Full_Time_Driver UNION SELECT person_id FROM Contract_Driver) ORDER BY full_name");
            while (rs.next()) map.put(rs.getInt(1), rs.getInt(1) + " - " + rs.getString(2));
            rs.close(); st.close();
        } catch (SQLException e) { UIHelper.err("Error fetching drivers:\n" + e.getMessage()); }
        return map;
    }

    public static Map<Integer, String> getDeliveries() {
        Map<Integer, String> map = new LinkedHashMap<>();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT delivery_id, CONCAT(delivery_id, ' - ', origin, ' → ', destination) as label FROM Delivery WHERE delivery_status != 'Completed' ORDER BY delivery_date DESC");
            while (rs.next()) map.put(rs.getInt(1), rs.getString(2));
            rs.close(); st.close();
        } catch (SQLException e) { UIHelper.err("Error fetching deliveries:\n" + e.getMessage()); }
        return map;
    }
}

