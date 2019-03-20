package org.donorcalendar.web;

import org.apache.http.HttpStatus;
import org.donorcalendar.AbstractRestAssuredIntegrationTest;
import org.donorcalendar.JacksonConfig;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.donorcalendar.util.IdGenerator;
import org.donorcalendar.web.dto.NewUserDto;
import org.donorcalendar.web.dto.UpdateUserDto;
import org.donorcalendar.web.dto.UpdateUserPasswordDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class UserControllerIT extends AbstractRestAssuredIntegrationTest {

    private static final String JOHN_UNENCRYPTED_PASSWORD = "pass1";
    private static final String JOHN_ENCRYPTED_PASSWORD = "$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a";
    private static final String BILBO_UNENCRYPTED_PASSWORD = "pass2";
    private static final String BILBO_ENCRYPTED_PASSWORD = "$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe";
    private static final String BASE_PATH = "/user";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(JacksonConfig.LOCAL_DATE_FORMAT);

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserSecurityDetailsRepository userSecurityDetailsRepository;

    private UserProfileEntity john;
    private UserProfileEntity bilbo;

    @Override
    public void setUp() {
        john = new UserProfileEntity();
        john.setUserId(IdGenerator.generateNewId());
        john.setName("John");
        john.setEmail("john@middlehearth.com");
        john.setBloodType(BloodType.AB_NEGATIVE);
        john.setLastDonation(LocalDate.now().minusDays(7));
        john.setDaysBetweenReminders(7);
        john.setNextReminder(LocalDate.now());
        john.setUserStatus(UserStatus.DONOR);

        bilbo = new UserProfileEntity();
        bilbo.setUserId(IdGenerator.generateNewId());
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
        bilbo.setLastDonation(LocalDate.now().minusDays(14));
        bilbo.setDaysBetweenReminders(14);
        bilbo.setNextReminder(LocalDate.now());
        bilbo.setUserStatus(UserStatus.DONOR);

        john = userProfileRepository.save(john);
        bilbo = userProfileRepository.save(bilbo);

        UserSecurityDetailsEntity userSecurityDetailsEntityJohn = new UserSecurityDetailsEntity();
        userSecurityDetailsEntityJohn.setUserId(john.getUserId());
        userSecurityDetailsEntityJohn.setPassword(JOHN_ENCRYPTED_PASSWORD);

        UserSecurityDetailsEntity userSecurityDetailsEntityBilbo = new UserSecurityDetailsEntity();
        userSecurityDetailsEntityBilbo.setUserId(bilbo.getUserId());
        userSecurityDetailsEntityBilbo.setPassword(BILBO_ENCRYPTED_PASSWORD);

        userSecurityDetailsRepository.save(userSecurityDetailsEntityJohn);
        userSecurityDetailsRepository.save(userSecurityDetailsEntityBilbo);
    }

    @Override
    public void tearDown() {
        userSecurityDetailsRepository.deleteAll();
        userProfileRepository.deleteAll();
    }

    @Test
    public void canGetUserJohnTest() {
        given().
                auth().basic(john.getEmail(), JOHN_UNENCRYPTED_PASSWORD).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                get(BASE_PATH).
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
            get(BASE_PATH).
        then().
            assertThat().
                body("name", equalTo(bilbo.getName())).
                body("email", equalTo(bilbo.getEmail()));
    }

    @Test
    public void canUpdateUserTest() {
        UpdateUserDto updateUserDto = userEntityToUserDto(bilbo);
        updateUserDto.setName("Bilbo Update");

        given().
                contentType(JSON_CONTENT_TYPE).
                body(updateUserDto).
                auth().basic(bilbo.getEmail(), BILBO_UNENCRYPTED_PASSWORD).
        expect().
                statusCode(HttpStatus.SC_OK).
                when().
                put(BASE_PATH);

        given().
                auth().basic(bilbo.getEmail(), BILBO_UNENCRYPTED_PASSWORD).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                get(BASE_PATH).
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
            contentType(JSON_CONTENT_TYPE).
            body(userPasswordDto).
            auth().basic(bilbo.getEmail(), BILBO_UNENCRYPTED_PASSWORD).
        expect().
            statusCode(HttpStatus.SC_NO_CONTENT).
        when().
            put(BASE_PATH + "/update-password");

        given().
            auth().basic(bilbo.getEmail(), newPassword).
        expect().
            statusCode(HttpStatus.SC_OK).
        when().
            get(BASE_PATH);
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
                contentType(JSON_CONTENT_TYPE).
                body(newUserDto).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                post(BASE_PATH);

        given().
                auth().basic(newUserDto.getEmail(), newUserDto.getPassword()).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                get(BASE_PATH).
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
                contentType(JSON_CONTENT_TYPE).
                body(newUserDto).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                post(BASE_PATH);

        given().
                contentType(JSON_CONTENT_TYPE).
                body(newUserDto).
        expect().
                statusCode(HttpStatus.SC_BAD_REQUEST).
        when().
                post(BASE_PATH);
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
