package org.donorcalendar.model;

import java.time.LocalDate;

public class UserProfile {

    private Long userId;

    private String name;
    private String email;
    private LocalDate lastDonation;
    private BloodType bloodType;
    private int daysBetweenReminders;
    private LocalDate nextReminder;
    private UserStatus userStatus;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public int getDaysBetweenReminders() {
        return daysBetweenReminders;
    }

    public void setDaysBetweenReminders(int daysBetweenReminders) {
        this.daysBetweenReminders = daysBetweenReminders;
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

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", lastDonation=" + lastDonation +
                ", bloodType=" + bloodType +
                ", daysBetweenReminders=" + daysBetweenReminders +
                ", nextReminder=" + nextReminder +
                ", userStatus=" + userStatus +
                '}';
    }
}
