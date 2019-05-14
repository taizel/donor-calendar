package org.donorcalendar;

import org.junit.runner.RunWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PersistenceIntegrationTestConfig.class},
        initializers = {AbstractPersistenceIntegrationTest.Initializer.class})
@ActiveProfiles("production")
public abstract class AbstractPersistenceIntegrationTest extends DatabaseContainerStarter {

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + databaseContainer.getJdbcUrl(),
                    "spring.datasource.username=" + databaseContainer.getUsername(),
                    "spring.datasource.password=" + databaseContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
