package org.donorcalendar.persistence;


import org.donorcalendar.domain.BloodType;
import org.donorcalendar.domain.UserProfile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class UserProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    private String name;
    @Column(unique=true)
    private String email;
    private LocalDate lastDonation;
    private BloodType bloodType;
    private int daysBetweenReminders;
    private LocalDate nextReminder;

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

    public UserProfile getUserDetails(){
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(this.getUserId());
        userProfile.setName(this.getName());
        userProfile.setEmail(this.getEmail());
        userProfile.setBloodType(this.getBloodType());
        userProfile.setDaysBetweenReminders(this.getDaysBetweenReminders());
        userProfile.setLastDonation(this.getLastDonation());
        userProfile.setNextReminder(this.getNextReminder());
        return userProfile;
    }
}
