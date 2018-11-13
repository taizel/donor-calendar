package org.donorcalendar.web.dto;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;

import java.time.LocalDate;

public class UserDto {
    private String name;
    private String email;
    private LocalDate lastDonation;
    private BloodType bloodType;
    private Integer daysBetweenReminders;
    private LocalDate nextReminder;
    private UserStatus userStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getLastDonation() {
        return lastDonation;
    }

    public void setLastDonation(LocalDate lastDonation) {
        this.lastDonation = lastDonation;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public Integer getDaysBetweenReminders() {
        return daysBetweenReminders;
    }

    public void setDaysBetweenReminders(Integer daysBetweenReminders) {
        this.daysBetweenReminders = daysBetweenReminders;
    }

    public LocalDate getNextReminder() {
        return nextReminder;
    }

    public void setNextReminder(LocalDate nextReminder) {
        this.nextReminder = nextReminder;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
