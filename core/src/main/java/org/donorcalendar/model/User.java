package org.donorcalendar.model;

public class User {
    private final UserProfile userProfile;
    private final UserCredentials userSecurity;

    public User(UserProfile userProfile, UserCredentials userSecurity) {
        this.userProfile = userProfile;
        this.userSecurity = userSecurity;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public UserCredentials getUserCredentials() {
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
