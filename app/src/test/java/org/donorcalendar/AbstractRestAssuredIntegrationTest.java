package org.donorcalendar;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractRestAssuredIntegrationTest extends AbstractPersistenceIntegrationTest {

    private static boolean isRestAssuredNotConfigured = true;
    @LocalServerPort
    protected int port;

    @Before
    public void completeSetUp() {
        // one time set-up to get around static restriction on @BeforeClass and use of instance @LocalServerPort
        if (isRestAssuredNotConfigured) {
            configureRestAssured(port);
        }
        setUp();
    }

    private static void configureRestAssured(int port) {
        RestAssured.port = port;
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory(
                        (classType, charset) -> JacksonConfig.getObjectMapper()
                ));
        isRestAssuredNotConfigured = false;
    }

    protected abstract void setUp();

    @After
    public abstract void tearDown();

    @AfterClass
    static public void staticTearDown() {
        RestAssured.reset();
        isRestAssuredNotConfigured = true;
    }
}
