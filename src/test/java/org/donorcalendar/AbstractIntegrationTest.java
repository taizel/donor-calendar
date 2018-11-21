package org.donorcalendar;

import java.util.Collections;

import org.junit.runner.RunWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = { AbstractIntegrationTest.Initializer.class})
public class AbstractIntegrationTest {

    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>().
            withCreateContainerCmdModifier(cmd ->
                    cmd.getHostConfig().withTmpFs(Collections.singletonMap(System.getProperty("java.io.tmpdir"), "rw"))
            );

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
