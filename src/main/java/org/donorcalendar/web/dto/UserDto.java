package org.donorcalendar.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy.class) //TODO why is this necessary and how to set it globally?
public class UserDto {
    private String name;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastDonation;
    private BloodType bloodType;
    private int daysBetweenReminders;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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

    public int getDaysBetweenReminders() {
        return daysBetweenReminders;
    }

    public void setDaysBetweenReminders(int daysBetweenReminders) {
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
