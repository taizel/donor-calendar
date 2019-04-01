package org.donorcalendar.rest.dto;

public class UpdateUserPasswordDto {
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String password) {
        this.newPassword = password;
    }
}