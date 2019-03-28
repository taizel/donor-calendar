package org.donorcalendar.model;

public class UserCredentials {
    private String password;

    public UserCredentials(String password) {
        this.password = password;
    }

    public UserCredentials(UserCredentials userCredentials) {
        this.password = userCredentials.password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}