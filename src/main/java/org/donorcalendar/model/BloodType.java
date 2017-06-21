package org.donorcalendar.model;

public enum BloodType {
    A_POSITIVE("A+"), A_NEGATIVE("A-"), B_POSITIVE("B+"), B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"), AB_NEGATIVE("AB-"), O_POSITIVE("O+"), O_NEGATIVE("O-");

    BloodType(String typeValue) {
        value = typeValue;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
