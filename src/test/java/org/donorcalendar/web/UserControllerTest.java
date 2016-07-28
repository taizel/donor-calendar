package org.donorcalendar.web;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.donorcalendar.Application;
import org.donorcalendar.domain.BloodType;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.donorcalendar.web.dto.NewUserDto;
import org.donorcalendar.web.dto.UpdateUserDto;
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

    @Autowired
    private UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Value("${local.server.port}")
    private int port;

    private UserProfileEntity john;
    private UserProfileEntity bilbo;

    @Before
    public void setUp() {
        john = new UserProfileEntity();
        john.setName("John");
        john.setEmail("john@middlehearth.com");
        john.setBloodType(BloodType.AB_NEGATIVE);
        john.setLastDonation(LocalDate.now().minusDays(7));
        john.setDaysBetweenReminders(7);
        john.setNextReminder(LocalDate.now());


        bilbo = new UserProfileEntity();
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
        bilbo.setLastDonation(LocalDate.now().minusDays(14));
        bilbo.setDaysBetweenReminders(14);
        bilbo.setNextReminder(LocalDate.now());

        repository.deleteAll();
        john = repository.save(john);
        bilbo = repository.save(bilbo);

        UserSecurityDetailsEntity userSecurityDetailsEntityJohn = new UserSecurityDetailsEntity();
        userSecurityDetailsEntityJohn.setUserId(john.getUserId());
        userSecurityDetailsEntityJohn.setPassword("$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a"); //pass1

        UserSecurityDetailsEntity userSecurityDetailsEntityBilbo = new UserSecurityDetailsEntity();
        userSecurityDetailsEntityBilbo.setUserId(bilbo.getUserId());
        userSecurityDetailsEntityBilbo.setPassword("$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe"); //pass2


        userSecurityDetailsRepository.save(userSecurityDetailsEntityJohn);
        userSecurityDetailsRepository.save(userSecurityDetailsEntityBilbo);

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
        UpdateUserDto updateUserDto = userToUserDto(bilbo);
        updateUserDto.setName("Bilbo Update");
        given().
                contentType("application/json").
                body(updateUserDto).
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
                body("name", equalTo(updateUserDto.getName()));
    }

    @Test
    public void canCreateNewUserTest(){
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
//        updateUserDto.setLastDonation("2016-01-15");
        newUserDto.setBloodType(BloodType.A_POSITIVE);

        given().
                contentType("application/json").
                body(newUserDto).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                post("/user");

        given().
                auth().basic(newUserDto.getEmail(), newUserDto.getPassword()).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                get("/user").
                then().
                assertThat().
                body("name", equalTo(newUserDto.getName())).
                body("email", equalTo(newUserDto.getEmail())).
                body("bloodType", equalTo(newUserDto.getBloodType().toString()));
    }


    private UpdateUserDto userToUserDto(UserProfileEntity user) {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setName(user.getName());
        updateUserDto.setBloodType(user.getBloodType());
        updateUserDto.setLastDonation(user.getLastDonation().toString());
        return updateUserDto;
    }
}
