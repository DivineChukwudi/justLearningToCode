package tt_logistics.model;

/**
 * Singleton session — holds the currently logged-in AppUser.
 * Call Session.get() from anywhere in the app.
 */
public class Session {
    private static AppUser currentUser = null;

    public static void login(AppUser user) { currentUser = user; }
    public static void logout()            { currentUser = null; }

    public static AppUser get()            { return currentUser; }
    public static boolean isLoggedIn()     { return currentUser != null; }

    public static boolean isAdmin()        { return isLoggedIn() && currentUser.isAdmin();   }
    public static boolean isDriver()       { return isLoggedIn() && currentUser.isDriver();  }
    public static boolean isManager()      { return isLoggedIn() && currentUser.isManager(); }
}
