package org.donorcalendar.web;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.donorcalendar.web.dto.NewUserDto;
import org.donorcalendar.web.dto.UpdateUserDto;
import org.donorcalendar.web.dto.UpdateUserPasswordDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIT {

    private final String JOHN_UNENCRYPTED_PASSWORD = "pass1";
    private final String JOHN_ENCRYPTED_PASSWORD = "$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a";
    private final String BILBO_UNENCRYPTED_PASSWORD = "pass2";
    private final String BILBO_ENCRYPTED_PASSWORD = "$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe";

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserSecurityDetailsRepository userSecurityDetailsRepository;

    @LocalServerPort
    private int port;

    private UserProfileEntity john;
    private UserProfileEntity bilbo;

    @Before
    public void setUp() {
        john = new UserProfileEntity();
        john.setUserId(System.currentTimeMillis() - 1);
        john.setName("John");
        john.setEmail("john@middlehearth.com");
        john.setBloodType(BloodType.AB_NEGATIVE);
        john.setLastDonation(LocalDate.now().minusDays(7));
        john.setDaysBetweenReminders(7);
        john.setNextReminder(LocalDate.now());
        john.setUserStatus(UserStatus.DONOR);

        bilbo = new UserProfileEntity();
        bilbo.setUserId(System.currentTimeMillis());
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
        bilbo.setLastDonation(LocalDate.now().minusDays(14));
        bilbo.setDaysBetweenReminders(14);
        bilbo.setNextReminder(LocalDate.now());
        bilbo.setUserStatus(UserStatus.DONOR);

        userProfileRepository.deleteAll();
        john = userProfileRepository.save(john);
        bilbo = userProfileRepository.save(bilbo);

        UserSecurityDetailsEntity userSecurityDetailsEntityJohn = new UserSecurityDetailsEntity();
        userSecurityDetailsEntityJohn.setUserId(john.getUserId());
        userSecurityDetailsEntityJohn.setPassword(JOHN_ENCRYPTED_PASSWORD);

        UserSecurityDetailsEntity userSecurityDetailsEntityBilbo = new UserSecurityDetailsEntity();
        userSecurityDetailsEntityBilbo.setUserId(bilbo.getUserId());
        userSecurityDetailsEntityBilbo.setPassword(BILBO_ENCRYPTED_PASSWORD);

        userSecurityDetailsRepository.deleteAll();
        userSecurityDetailsRepository.save(userSecurityDetailsEntityJohn);
        userSecurityDetailsRepository.save(userSecurityDetailsEntityBilbo);

        RestAssured.port = port;
    }

    @After
    public void cleanUp() {
        RestAssured.reset();
    }

    @Test
    public void canGetUserJohnTest() {
        given().
                auth().basic(john.getEmail(), JOHN_UNENCRYPTED_PASSWORD).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                get("/user").
        then().
                assertThat().
                body("name", equalTo(john.getName())).
                body("email", equalTo(john.getEmail())).
                body("blood-type", equalTo(john.getBloodType().toString())).
                body("last-donation", equalTo(john.getLastDonation().format(DATE_FORMATTER))).
                body("days-between-reminders", equalTo(john.getDaysBetweenReminders())).
                body("next-reminder", equalTo(john.getNextReminder().format(DATE_FORMATTER))).
                body("user-status", equalTo(john.getUserStatus().toString()));
    }

    @Test
    public void canGetUserBilboTest() {
        given().
            auth().basic(bilbo.getEmail(), BILBO_UNENCRYPTED_PASSWORD).
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
    public void canUpdateUserTest() {
        UpdateUserDto updateUserDto = userEntityToUserDto(bilbo);
        updateUserDto.setName("Bilbo Update");
        RestAssured.authentication = basic(bilbo.getEmail(), BILBO_UNENCRYPTED_PASSWORD);

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
    public void canUpdateUserPasswordTest() {
        UpdateUserPasswordDto userPasswordDto = new UpdateUserPasswordDto();
        String newPassword = "passwordUpdate";
        userPasswordDto.setNewPassword(newPassword);
        given().
            contentType("application/json").
            body(userPasswordDto).
            auth().basic(bilbo.getEmail(), BILBO_UNENCRYPTED_PASSWORD).
        expect().
            statusCode(HttpStatus.SC_NO_CONTENT).
        when().
            put("/user/update-password");

        given().
            auth().basic(bilbo.getEmail(), newPassword).
        expect().
            statusCode(HttpStatus.SC_OK).
        when().
            get("/user");
    }

    @Test
    public void canCreateNewUserTest() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(2));
        newUserDto.setBloodType(BloodType.A_POSITIVE);
        newUserDto.setUserStatus(UserStatus.fromNumberOfElapsedDaysSinceLastDonation(60));

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
                body("blood-type", equalTo(newUserDto.getBloodType().toString()));
    }

    @Test
    public void validateEmailAlreadyTakenForNewUserTest() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusDays(2));
        newUserDto.setBloodType(BloodType.A_POSITIVE);

        given().
                contentType("application/json").
                body(newUserDto).
                expect().
                statusCode(HttpStatus.SC_OK).
                when().
                post("/user");

        given().
                contentType("application/json").
                body(newUserDto).
                expect().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                when().
                post("/user");
    }

    private UpdateUserDto userEntityToUserDto(UserProfileEntity user) {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setName(user.getName());
        updateUserDto.setBloodType(user.getBloodType());
        updateUserDto.setLastDonation(user.getLastDonation());
        updateUserDto.setUserStatus(user.getUserStatus());
        return updateUserDto;
    }
}
