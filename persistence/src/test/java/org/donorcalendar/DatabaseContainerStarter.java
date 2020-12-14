package org.donorcalendar;

import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;

public class DatabaseContainerStarter {

    protected static final Network databaseContainerNetwork = Network.newNetwork();

    protected static final PostgreSQLContainer databaseContainer = new PostgreSQLContainer<>("postgres").
            withNetwork(databaseContainerNetwork).
            withNetworkAliases("db-donor-calendar").
            withTmpFs(Collections.singletonMap(System.getProperty("java.io.tmpdir"), "rw")).
            // DB connection properties
            withDatabaseName("donor").
            withUsername("donor").
            withPassword("donor-password");

    static {
        databaseContainer.start();
    }
}
