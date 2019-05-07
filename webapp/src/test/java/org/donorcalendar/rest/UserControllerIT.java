package org.donorcalendar.rest;

import org.apache.http.HttpStatus;
import org.donorcalendar.JacksonConfig;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserCredentialsDaoInMemoryImpl;
import org.donorcalendar.persistence.UserProfileDaoInMemoryImpl;
import org.donorcalendar.service.UserCredentialsService;
import org.donorcalendar.util.IdGenerator;
import org.donorcalendar.rest.dto.NewUserDto;
import org.donorcalendar.rest.dto.UpdateUserDto;
import org.donorcalendar.rest.dto.UpdateUserPasswordDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class UserControllerIT extends AbstractRestAssuredIntegrationTest {

    private static final String BILBO_PASSWORD = "pass2";
    private static final String BASE_PATH = "/user";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(JacksonConfig.LOCAL_DATE_FORMAT);

    @Autowired
    private UserProfileDaoInMemoryImpl userProfileDao;
    @Autowired
    private UserCredentialsDaoInMemoryImpl userCredentialsDao;
    private final PasswordEncoder passwordEncoder = UserCredentialsService.getNewPasswordEncoder();

    private UserProfile bilbo;

    @Override
    public void setUp() {
        bilbo = new UserProfile();
        bilbo.setUserId(IdGenerator.generateNewId());
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
        bilbo.setLastDonation(LocalDate.now().minusDays(14));
        bilbo.setDaysBetweenReminders(14);
        bilbo.setNextReminder(LocalDate.now());
        bilbo.setUserStatus(UserStatus.DONOR);

        bilbo = userProfileDao.saveNewUser(bilbo);

        UserCredentials userCredentialsEntityBilbo = new UserCredentials(passwordEncoder.encode(BILBO_PASSWORD));

        userCredentialsDao.saveNewUserCredentials(bilbo.getUserId(), userCredentialsEntityBilbo);
    }

    @Override
    public void tearDown() {
        userProfileDao.deleteAll();
        userCredentialsDao.deleteAll();
    }

    @Test
    public void canGetUser() {
        given().
            auth().basic(bilbo.getEmail(), BILBO_PASSWORD).
        expect().
            statusCode(HttpStatus.SC_OK).
        when().
            get(BASE_PATH).
        then().
            assertThat().
                body("name", equalTo(bilbo.getName())).
                body("email", equalTo(bilbo.getEmail())).
                body("bloodType", equalTo(bilbo.getBloodType().toString())).
                body("lastDonation", equalTo(bilbo.getLastDonation().format(DATE_FORMATTER))).
                body("daysBetweenReminders", equalTo(bilbo.getDaysBetweenReminders())).
                body("nextReminder", equalTo(bilbo.getNextReminder().format(DATE_FORMATTER))).
                body("userStatus", equalTo(bilbo.getUserStatus().toString()));
    }

    @Test
    public void canUpdateUser() {
        UpdateUserDto updateUserDto = userModelToUserDto(bilbo);
        updateUserDto.setName("Bilbo Update");

        given().
                contentType(JSON_CONTENT_TYPE).
                body(updateUserDto).
                auth().basic(bilbo.getEmail(), BILBO_PASSWORD).
        expect().
                statusCode(HttpStatus.SC_OK).
                when().
                put(BASE_PATH);

        given().
                auth().basic(bilbo.getEmail(), BILBO_PASSWORD).
        expect().
                statusCode(HttpStatus.SC_OK).
        when().
                get(BASE_PATH).
        then().
            assertThat().
                body("name", equalTo(updateUserDto.getName()));
    }

    @Test
    public void canUpdateUserPassword() {
        UpdateUserPasswordDto userPasswordDto = new UpdateUserPasswordDto();
        String newPassword = "passwordUpdate";
        userPasswordDto.setNewPassword(newPassword);
        given().
            contentType(JSON_CONTENT_TYPE).
            body(userPasswordDto).
            auth().basic(bilbo.getEmail(), BILBO_PASSWORD).
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
    public void canCreateNewUser() {
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
                body("bloodType", equalTo(newUserDto.getBloodType().toString()));
    }

    private UpdateUserDto userModelToUserDto(UserProfile user) {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setName(user.getName());
        updateUserDto.setBloodType(user.getBloodType());
        updateUserDto.setLastDonation(user.getLastDonation());
        updateUserDto.setUserStatus(user.getUserStatus());
        return updateUserDto;
    }
}
