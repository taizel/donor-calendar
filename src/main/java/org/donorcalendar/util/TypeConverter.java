package org.donorcalendar.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TypeConverter {

    /***
     *
     * @param dateString in the format yyyy-MM-dd
     * @return the converted LocalDate type
     */
    public static LocalDate stringToLocalDate(String dateString) throws DateTimeParseException{
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, formatter);
    }
}
