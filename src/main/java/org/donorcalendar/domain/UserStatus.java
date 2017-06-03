package org.donorcalendar.domain;

public enum UserStatus {

    DONOR("Donor"), // Donated blood less or equal than 56 days ago
    POTENTIAL_DONOR("Potential Donor"), // Donated more than 56 days ago and less 120 days
    NEED_TO_DONATE("Need to Donate"); // More than 120 days

    private String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static UserStatus getStatusByNumberOfDaysSinceLastDonation(long numberOfDays) {
        if(numberOfDays <= 56){
            return DONOR;
        }else if (numberOfDays <= 120){
            return POTENTIAL_DONOR;
        } else {
            return NEED_TO_DONATE;
        }
    }
}
