package org.donorcalendar;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.Before;
import org.springframework.boot.context.embedded.LocalServerPort;

public abstract class RestAssuredTestTemplate {

    @LocalServerPort
    protected int port;

    @Before
    public void completeSetUp() {
        RestAssuredSetup();
        businessSetUp();
    }

    private void RestAssuredSetup() {
        RestAssured.port = port;
        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (classType, charset) -> MvcConfig.getObjectMapper()
        ));
    }

    public abstract void businessSetUp();

}
