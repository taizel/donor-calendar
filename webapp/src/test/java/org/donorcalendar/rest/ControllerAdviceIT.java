package org.donorcalendar.rest;

import static io.restassured.RestAssured.given;

import java.time.LocalDate;

import org.apache.http.HttpStatus;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.rest.dto.NewUserDto;
import org.junit.Test;

import io.restassured.http.ContentType;

public class ControllerAdviceIT extends AbstractRestAssuredIntegrationTest {

	@Override
	protected void setUp() {
	}

	@Override
	public void tearDown() {
	}

	@Test
	public void validationErrorTest() {
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
	public void forbiddenAccessErrorTest() {
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
