package org.donorcalendar.persistence;


import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "user_profile")
public class UserProfileEntity {

    @Id
    private Long userId;

    @NotNull
    private String name;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private BloodType bloodType;
    @NotNull
    private UserStatus userStatus;

    private LocalDate lastDonation;
    private Integer daysBetweenReminders;
    private LocalDate nextReminder;

    protected UserProfileEntity() {
    }

    UserProfileEntity(UserProfile userProfile) {
        this.userId = userProfile.getUserId();
        this.name = userProfile.getName();
        this.email = userProfile.getEmail();
        this.bloodType = userProfile.getBloodType();
        this.daysBetweenReminders = userProfile.getDaysBetweenReminders();
        this.lastDonation = userProfile.getLastDonation();
        this.nextReminder = userProfile.getNextReminder();
        this.userStatus = userProfile.getUserStatus();
    }

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

    UserProfile getUserProfile() {
        UserProfile.UserProfileBuilder builder = new UserProfile.UserProfileBuilder(
                userId,
                name,
                email,
                bloodType,
                userStatus
        );
        return builder.daysBetweenReminders(daysBetweenReminders)
                .lastDonation(lastDonation)
                .nextReminder(nextReminder)
                .build();

    }
}
