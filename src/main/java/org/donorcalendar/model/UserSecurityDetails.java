package org.donorcalendar.model;

public class UserSecurityDetails {
    private String password;

    public UserSecurityDetails(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}