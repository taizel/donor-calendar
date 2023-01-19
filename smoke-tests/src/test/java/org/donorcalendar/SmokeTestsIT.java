package org.donorcalendar;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.rest.dto.NewUserDto;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.LocalDate;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SmokeTestsIT extends DatabaseContainerStarter {

    private static final GenericContainer donorCalendarWebApp = new GenericContainer("donor-calendar:latest").
            withNetwork(databaseContainer.getNetwork()).
            withExposedPorts(8080).
            withEnv("DB_HOST", DB_HOST).
            withEnv("DB_PORT", "5432").
            withEnv("DB_NAME", databaseContainer.getDatabaseName()).
            withEnv("DB_USERNAME", databaseContainer.getUsername()).
            withEnv("DB_PASSWORD", databaseContainer.getPassword()).
            waitingFor(Wait.forLogMessage(".*" + Application.APPLICATION_READY + "\\n", 1)).
            withTmpFs(Collections.singletonMap(System.getProperty("java.io.tmpdir"), "rw"));

    private static final String USER_PATH = "/user";

    @BeforeClass
    public static void setUp() {
        donorCalendarWebApp.start();

        RestAssured.port = donorCalendarWebApp.getMappedPort(8080);
        // some CI environments may be reachable on a different host
        RestAssured.baseURI = "http://" + donorCalendarWebApp.getHost();
    }

    @Test
    public void canCreateNewUser() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(2));
        newUserDto.setBloodType(BloodType.A_POSITIVE);

        given().
                contentType(ContentType.JSON).
                body(newUserDto).
                expect().
                statusCode(HttpStatus.SC_OK).
                when().
                post(USER_PATH);

        given().
                auth().basic(newUserDto.getEmail(), newUserDto.getPassword()).
                expect().
                statusCode(HttpStatus.SC_OK).
                when().
                get(USER_PATH).
                then().
                assertThat().
                body("name", equalTo(newUserDto.getName())).
                body("email", equalTo(newUserDto.getEmail())).
                body("bloodType", equalTo(newUserDto.getBloodType().toString()));
    }

    @Test
    public void forbiddenAccessErrorTest() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("forbidden@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(5));
        newUserDto.setBloodType(BloodType.A_POSITIVE);

        given().
                contentType(ContentType.JSON).
                body(newUserDto).
                expect().
                statusCode(HttpStatus.SC_OK).
                when().
                post(USER_PATH);

        given().
                auth().basic(newUserDto.getEmail(), newUserDto.getPassword()).
                expect().
                statusCode(HttpStatus.SC_FORBIDDEN).
                when().
                get("/donor-zone");
    }
}