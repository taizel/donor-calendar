package org.donorcalendar.rest.dto;

public class NewUserDto extends UserDto {

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}