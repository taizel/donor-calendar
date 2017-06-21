package org.donorcalendar.model;

public class User {
    private UserProfile userProfile;
    private UserSecurityDetails userSecurity;

    public User(UserProfile userProfile, UserSecurityDetails userSecurity) {
        this.userProfile = userProfile;
        this.userSecurity = userSecurity;
    }

    public User(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserSecurityDetails getUserSecurity() {
        return userSecurity;
    }

    public void setUserSecurity(UserSecurityDetails userSecurity) {
        this.userSecurity = userSecurity;
    }

    @Override
    public String toString() {
        return "User{" +
                "userProfile=" + userProfile +
                ", userSecurity=" + userSecurity +
                '}';
    }
}
