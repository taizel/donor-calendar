package org.donorcalendar;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractRestAssuredIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    protected int port;
    private boolean isRestAssuredNotConfigured = true;

    @Before
    public void completeSetUp() {
        if (isRestAssuredNotConfigured) {
            configureRestAssured();
        }
        businessSetUp();
    }

    private void configureRestAssured() {
        RestAssured.port = port;
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (classType, charset) -> JacksonConfig.getObjectMapper()
        ));
        isRestAssuredNotConfigured = false;
    }

    protected abstract void businessSetUp();
}
