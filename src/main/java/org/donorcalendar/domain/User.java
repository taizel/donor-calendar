package org.donorcalendar.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String email;
    private LocalDate lastDonation;
    private BloodType bloodType;
    private int intervalOfDaysBetweenReminders;

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

    public int getIntervalOfDaysBetweenReminders() {
        return intervalOfDaysBetweenReminders;
    }

    public void setIntervalOfDaysBetweenReminders(int intervalOfDaysBetweenReminders) {
        this.intervalOfDaysBetweenReminders = intervalOfDaysBetweenReminders;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", lastDonation=" + lastDonation +
                ", bloodType=" + bloodType +
                ", intervalOfDaysBetweenReminders=" + intervalOfDaysBetweenReminders +
                '}';
    }
}
