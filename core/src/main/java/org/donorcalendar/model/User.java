package org.donorcalendar.model;

public class User {
    private UserProfile userProfile;
    private UserSecurityDetails userSecurity;

    public User(UserProfile userProfile, UserSecurityDetails userSecurity) {
        this.userProfile = userProfile;
        this.userSecurity = userSecurity;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public UserSecurityDetails getUserSecurity() {
        return userSecurity;
    }

    @Override
    public String toString() {
        return "User{" +
                "userProfile=" + userProfile +
                ", userSecurity=" + userSecurity +
                '}';
    }
}
