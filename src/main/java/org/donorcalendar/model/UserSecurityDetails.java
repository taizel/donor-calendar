package org.donorcalendar.model;

public class UserSecurityDetails {
    private String password;

    public UserSecurityDetails(String password) {
        this.password = password;
    }

    public UserSecurityDetails(UserSecurityDetails userSecurityDetails) {
        this.password = userSecurityDetails.password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}