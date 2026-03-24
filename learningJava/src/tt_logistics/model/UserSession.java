package tt_logistics.model;

/**
 * Holds the currently logged-in user's session data.
 * Single instance shared across the whole app.
 */
public class UserSession {

    public enum Role { ADMIN, DRIVER, MANAGER }

    private static UserSession instance;

    private int    userID;
    private String username;
    private Role   role;
    private int    personID;   // -1 if not linked to a person

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void login(int userID, String username, String role, int personID) {
        this.userID   = userID;
        this.username = username;
        this.role     = Role.valueOf(role.toUpperCase());
        this.personID = personID;
    }

    public void logout() {
        userID   = 0;
        username = null;
        role     = null;
        personID = -1;
    }

    // ── Convenience checks ───────────────────────────────────────────────────
    public boolean isAdmin()   { return role == Role.ADMIN; }
    public boolean isDriver()  { return role == Role.DRIVER; }
    public boolean isManager() { return role == Role.MANAGER; }

    // ── Getters ──────────────────────────────────────────────────────────────
    public int    getUserID()   { return userID; }
    public String getUsername() { return username; }
    public Role   getRole()     { return role; }
    public int    getPersonID() { return personID; }

    public String getRoleLabel() {
        return switch (role) {
            case ADMIN   -> "Administrator";
            case DRIVER  -> "Driver";
            case MANAGER -> "Fleet Manager";
        };
    }

    public String getRoleColor() {
        return switch (role) {
            case ADMIN   -> "#1E90FF";
            case DRIVER  -> "#00C896";
            case MANAGER -> "#F5A623";
        };
    }
}
