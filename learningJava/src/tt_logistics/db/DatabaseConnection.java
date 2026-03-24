package tt_logistics.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL    = "jdbc:mysql://localhost:3306/tt_logistics"
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Connection connection;
    private static String currentUser     = "root";
    private static String currentPassword = "divinea240872";

    public static void setCredentials(String user, String password) {
        currentUser     = user;
        currentPassword = password;
        closeConnection();
    }

    public static Connection getConnection() throws SQLException {
        try { Class.forName(DRIVER); }
        catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to classpath.", e);
        }
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, currentUser, currentPassword);
        }
        return connection;
    }

    public static void closeConnection() {
        try { if (connection != null && !connection.isClosed()) connection.close(); }
        catch (SQLException ignored) {}
        connection = null;
    }

    public static String getCurrentUser() { return currentUser; }
}
