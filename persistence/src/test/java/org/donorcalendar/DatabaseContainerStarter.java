package org.donorcalendar;

import org.testcontainers.containers.Network;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Collections;

public abstract class DatabaseContainerStarter {

    protected static final String DB_HOST = "db-donor-calendar";

    protected static final PostgreSQLContainer databaseContainer = new PostgreSQLContainer("postgres:18").
            withNetwork(Network.newNetwork()).
            withNetworkAliases(DB_HOST).
            withTmpFs(Collections.singletonMap(System.getProperty("java.io.tmpdir"), "rw")).
            // DB connection properties
            withDatabaseName("donor").
            withUsername("donor").
            withPassword("donor-password");

    static {
        databaseContainer.start();
    }
}
