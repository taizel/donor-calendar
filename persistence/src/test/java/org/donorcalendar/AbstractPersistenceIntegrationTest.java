package org.donorcalendar;

import org.junit.runner.RunWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PersistenceIntegrationTestConfig.class},
        initializers = {AbstractPersistenceIntegrationTest.Initializer.class})
@ActiveProfiles("production")
public abstract class AbstractPersistenceIntegrationTest {

    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>().
            withTmpFs(Collections.singletonMap(System.getProperty("java.io.tmpdir"), "rw"));

    static {
        postgreSQLContainer.withDatabaseName("donor");
        postgreSQLContainer.withUsername("donor");
        postgreSQLContainer.withPassword("donor-password");
        postgreSQLContainer.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
