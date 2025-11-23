package org.donorcalendar.rest;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.donorcalendar.JacksonConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"org.donorcalendar.scheduling.enable = false"}) // disable scheduled jobs
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractRestAssuredIntegrationTest {

    private static boolean isRestAssuredNotConfigured = true;
    @LocalServerPort
    protected int port;

    @AfterAll
    public static void staticTearDown() {
        RestAssured.reset();
        isRestAssuredNotConfigured = true;
    }

    @BeforeEach
    public void completeSetUp() {
        setUp();
        // one time set-up to get around static restriction on @BeforeClass and use of instance @LocalServerPort
        if (isRestAssuredNotConfigured) {
            configureRestAssured();
        }
    }

    protected abstract void setUp();

    private void configureRestAssured() {
        RestAssured.port = port;
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory(
                        (classType, charset) -> JacksonConfig.getNewCustomObjectMapper()
                ));
        isRestAssuredNotConfigured = false;
    }

    @AfterEach
    public abstract void tearDown();
}
