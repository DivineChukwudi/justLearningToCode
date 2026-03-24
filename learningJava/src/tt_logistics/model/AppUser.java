package tt_logistics.model;

public class AppUser {
    private final int    userID;
    private       String username;
    private       String role;
    private final int    personID;
    private       String fullName;
    private       String nickname;
    private       String avatarColor;

    public AppUser(int userID, String username, String role, int personID,
                   String fullName, String nickname, String avatarColor) {
        this.userID      = userID;
        this.username    = username;
        this.role        = role;
        this.personID    = personID;
        this.fullName    = fullName == null ? username : fullName;
        this.nickname    = nickname == null ? "" : nickname;
        this.avatarColor = avatarColor == null ? "#1E90FF" : avatarColor;
    }

    // Getters
    public int    getUserID()     { return userID;      }
    public String getUsername()   { return username;    }
    public String getRole()       { return role;        }
    public int    getPersonID()   { return personID;    }
    public String getFullName()   { return fullName;    }
    public String getNickname()   { return nickname;    }
    public String getAvatarColor(){ return avatarColor; }

    /** Display name: nickname if set, otherwise first name */
    public String getDisplayName() {
        if (nickname != null && !nickname.isBlank()) return nickname;
        String[] parts = fullName.split(" ");
        return parts[0];
    }

    /** Initials for avatar circle */
    public String getInitials() {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
    }

    // Setters (for profile edits)
    public void setFullName(String v)    { this.fullName    = v; }
    public void setNickname(String v)    { this.nickname    = v; }
    public void setAvatarColor(String v) { this.avatarColor = v; }

    public boolean isAdmin()   { return "Admin".equals(role);   }
    public boolean isDriver()  { return "Driver".equals(role);  }
    public boolean isManager() { return "Manager".equals(role); }
}
