package org.donorcalendar.model;

public enum BloodType {
    A_POSITIVE("A+"), A_NEGATIVE("A-"), B_POSITIVE("B+"), B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"), AB_NEGATIVE("AB-"), O_POSITIVE("O+"), O_NEGATIVE("O-");

    private final String value;

    BloodType(String typeValue) {
        value = typeValue;
    }

    public String getValue() {
        return value;
    }
}
