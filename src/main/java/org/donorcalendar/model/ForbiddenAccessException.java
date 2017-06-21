package org.donorcalendar.model;

public class ForbiddenAccessException extends Exception {
    public ForbiddenAccessException(String message) {
        super(message);
    }
}
