package org.donorcalendar.model;

import java.io.Serializable;
import java.time.LocalDate;

public final class UserProfile implements Serializable {

    private final long userId;
    private final String name;
    private final String email;
    private final BloodType bloodType;
    private final UserStatus userStatus;
    //optional fields
    private final LocalDate lastDonation;
    private final Integer daysBetweenReminders;
    private final LocalDate nextReminder;

    private UserProfile(UserProfileBuilder builder) {
        this.userId = builder.userId;
        this.name = builder.name;
        this.email = builder.email;
        this.lastDonation = builder.lastDonation;
        this.bloodType = builder.bloodType;
        this.daysBetweenReminders = builder.daysBetweenReminders;
        this.nextReminder = builder.nextReminder;
        this.userStatus = builder.userStatus;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public LocalDate getLastDonation() {
        return lastDonation;
    }

    public Integer getDaysBetweenReminders() {
        return daysBetweenReminders;
    }

    public LocalDate getNextReminder() {
        return nextReminder;
    }

    public static class UserProfileBuilder {
        private Long userId;
        private String name;
        private String email;
        private BloodType bloodType;
        private UserStatus userStatus;
        private LocalDate lastDonation;
        private Integer daysBetweenReminders;
        private LocalDate nextReminder;

        public UserProfileBuilder(long userId, String name, String email, BloodType bloodType, UserStatus userStatus) {
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.bloodType = bloodType;
            this.userStatus = userStatus;
        }

        public UserProfileBuilder(UserProfile userProfile) {
            this.userId = userProfile.userId;
            this.name = userProfile.name;
            this.email = userProfile.email;
            this.lastDonation = userProfile.lastDonation;
            this.bloodType = userProfile.bloodType;
            this.daysBetweenReminders = userProfile.daysBetweenReminders;
            this.nextReminder = userProfile.nextReminder;
            this.userStatus = userProfile.userStatus;
        }

        public UserProfileBuilder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public UserProfileBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserProfileBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileBuilder bloodType(BloodType bloodType) {
            this.bloodType = bloodType;
            return this;
        }

        public UserProfileBuilder userStatus(UserStatus userStatus) {
            this.userStatus = userStatus;
            return this;
        }

        public UserProfileBuilder lastDonation(LocalDate lastDonation) {
            this.lastDonation = lastDonation;
            return this;
        }

        public UserProfileBuilder daysBetweenReminders(Integer daysBetweenReminders) {
            this.daysBetweenReminders = daysBetweenReminders;
            return this;
        }

        public UserProfileBuilder nextReminder(LocalDate nextReminder) {
            this.nextReminder = nextReminder;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(this);
        }
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", bloodType=" + bloodType +
                ", userStatus=" + userStatus +
                ", lastDonation=" + lastDonation +
                ", daysBetweenReminders=" + daysBetweenReminders +
                ", nextReminder=" + nextReminder +
                '}';
    }
}
