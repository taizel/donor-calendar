package org.donorcalendar.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private IdGenerator() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }

    private static final AtomicLong lastIdGenerated = new AtomicLong(System.currentTimeMillis());

    public static long generateNewId(){
        return lastIdGenerated.getAndIncrement();
    }
}
