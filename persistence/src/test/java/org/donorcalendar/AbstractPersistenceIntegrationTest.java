package org.donorcalendar;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
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
