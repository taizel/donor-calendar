package org.donorcalendar.model;

public enum UserStatus {

    DONOR, // Donated blood less or equal than 56 days ago
    POTENTIAL_DONOR, // Donated more than 56 days ago and less 120 days
    NEED_TO_DONATE; // More than 120 days

    public static UserStatus fromNumberOfElapsedDaysSinceLastDonation(long numberOfDays) {
        if(numberOfDays <= 56){
            return DONOR;
        }else if (numberOfDays <= 120){
            return POTENTIAL_DONOR;
        } else {
            return NEED_TO_DONATE;
        }
    }
}
