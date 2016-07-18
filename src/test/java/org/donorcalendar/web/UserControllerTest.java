package org.donorcalendar.web;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.donorcalendar.Application;
import org.donorcalendar.domain.BloodType;
import org.donorcalendar.domain.User;
import org.donorcalendar.persistence.UserRepository;
import org.donorcalendar.web.dto.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.util.Arrays;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class UserControllerTest {

    @Autowired
    private UserRepository repository;

    @Value("${local.server.port}")
    private int port;

    private User john;
    private User bilbo;

    @Before
    public void setUp() {
        john = new User();
        john.setName("John");
        john.setEmail("john@middlehearth.com");
        john.setBloodType(BloodType.AB_NEGATIVE);
        john.setLastDonation(LocalDate.now().minusDays(7));
        john.setDaysBetweenReminders(7);
        john.setNextReminder(LocalDate.now());
        john.setPassword("$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a"); //pass1


        bilbo = new User();
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
        bilbo.setLastDonation(LocalDate.now().minusDays(14));
        bilbo.setDaysBetweenReminders(14);
        bilbo.setNextReminder(LocalDate.now());
        bilbo.setPassword("$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe");//pass2

        repository.deleteAll();
        repository.save(Arrays.asList(john, bilbo));

        RestAssured.port = port;
        RestAssured.authentication = basic(bilbo.getEmail(), "pass2");
    }

    @Test
    public void canGetUserJohnTest() {
        given().
                auth().basic(john.getEmail(), "pass1").
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                get("/user").
        then().
                assertThat().
                body("name", equalTo(john.getName())).
                body("email", equalTo(john.getEmail()));
    }

    @Test
    public void canGetUserBilboTest() {
        given().
            auth().basic(bilbo.getEmail(), "pass2").
        expect().
            statusCode(HttpStatus.SC_OK).
        when().
            get("/user").
        then().
            assertThat().
                body("name", equalTo(bilbo.getName())).
                body("email", equalTo(bilbo.getEmail()));
    }

    @Test
    public void canUpdateUserTest(){
        UserDto userDto = userToUserDto(bilbo);
        userDto.setName("Bilbo Update");
        given().
                contentType("application/json").
                body(userDto).
        expect().
                statusCode(HttpStatus.SC_OK).
                when().
                put("/user");

        expect().
                statusCode(HttpStatus.SC_OK).
                when().
                get("/user").
                then().
                assertThat().
                body("name", equalTo(userDto.getName()));
    }

    @Test
    public void canCreateNewUserTest(){
        UserDto userDto = new UserDto();
        userDto.setName("New");
        userDto.setEmail("new@newuser.com");
        userDto.setPassword("new");
//        userDto.setLastDonation("2016-01-15");
        userDto.setBloodType(BloodType.A_POSITIVE);

        given().
                contentType("application/json").
                body(userDto).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                post("/user");

        given().
                auth().basic(userDto.getEmail(), userDto.getPassword()).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                get("/user").
                then().
                assertThat().
                body("name", equalTo(userDto.getName())).
                body("email", equalTo(userDto.getEmail())).
                body("bloodType", equalTo(userDto.getBloodType().toString()));
    }


    private UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setBloodType(user.getBloodType());
        userDto.setLastDonation(user.getLastDonation().toString());
        return userDto;
    }
}
