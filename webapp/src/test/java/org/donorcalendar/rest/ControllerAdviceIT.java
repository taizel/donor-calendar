package org.donorcalendar.rest;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.rest.dto.NewUserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;

class ControllerAdviceIT extends AbstractRestAssuredIntegrationTest {

    @Override
    protected void setUp() {
        // Empty implementation, require to extend AbstractRestAssuredIntegrationTest
    }

    @AfterEach
    @Override
    public void tearDown() {
        // Empty implementation, require to extend AbstractRestAssuredIntegrationTest
    }

    @Test
    void validationErrorTest() {
        NewUserDto newUserDto = new NewUserDto();

        given().
                contentType(ContentType.JSON).
                body(newUserDto).
        expect().
                statusCode(HttpStatus.SC_BAD_REQUEST).
        when().
                post("/user");
    }

    @Test
    void forbiddenAccessErrorTest() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(5));
        newUserDto.setBloodType(BloodType.A_POSITIVE);

        given().
                contentType(ContentType.JSON).
                body(newUserDto).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                post("/user");

        given().
                auth().basic(newUserDto.getEmail(), newUserDto.getPassword()).
        expect().
                statusCode(HttpStatus.SC_FORBIDDEN).
        when().
                get("/donor-zone");
    }
}
