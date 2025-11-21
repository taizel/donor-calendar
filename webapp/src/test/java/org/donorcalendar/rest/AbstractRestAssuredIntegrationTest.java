package org.donorcalendar.rest;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.donorcalendar.JacksonConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {"org.donorcalendar.scheduling.enable = false"}) // disable scheduled jobs
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractRestAssuredIntegrationTest {

    private static boolean isRestAssuredNotConfigured = true;
    @LocalServerPort
    protected int port;

    @AfterClass
    public static void staticTearDown() {
        RestAssured.reset();
        isRestAssuredNotConfigured = true;
    }

    @Before
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

    @After
    public abstract void tearDown();
}
