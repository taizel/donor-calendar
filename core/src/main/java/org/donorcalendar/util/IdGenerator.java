package org.donorcalendar.util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

    private static final AtomicLong lastIdGenerated = new AtomicLong(System.currentTimeMillis());

    private IdGenerator() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }

    public static long generateNewId() {
        return lastIdGenerated.getAndIncrement();
    }
}
