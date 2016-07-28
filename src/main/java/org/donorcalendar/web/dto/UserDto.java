package org.donorcalendar.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.donorcalendar.domain.BloodType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String name;
    private String email;
    private String lastDonation;
    private BloodType bloodType;

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

    public String getLastDonation() {
        return lastDonation;
    }

    public void setLastDonation(String lastDonation) {
        this.lastDonation = lastDonation;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }
}
