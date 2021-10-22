package org.donorcalendar.model;

public class UserCredentials {
    private final String password;

    public UserCredentials(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}