package org.donorcalendar.rest;

import org.apache.http.HttpStatus;
import org.donorcalendar.JacksonConfig;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.FakeUserCredentialsDao;
import org.donorcalendar.persistence.FakeUserProfileDao;
import org.donorcalendar.rest.dto.NewUserDto;
import org.donorcalendar.rest.dto.UpdateUserDto;
import org.donorcalendar.rest.dto.UpdateUserPasswordDto;
import org.donorcalendar.service.UserCredentialsService;
import org.donorcalendar.util.IdGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserControllerIT extends AbstractRestAssuredIntegrationTest {

    private static final String TEST_PASSWORD = "pass2";
    private static final String BASE_PATH = "/user";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(JacksonConfig.LOCAL_DATE_FORMAT);
    private final PasswordEncoder passwordEncoder = UserCredentialsService.getNewPasswordEncoder();
    @Autowired
    private FakeUserProfileDao userProfileDao;
    @Autowired
    private FakeUserCredentialsDao userCredentialsDao;
    private UserProfile testUserProfile;

    @Override
    public void setUp() {
        UserProfile.UserProfileBuilder userProfileBuilder = new UserProfile.UserProfileBuilder(
                IdGenerator.generateNewId(),
                "Bilbo",
                "bilbo@middlehearth.com",
                BloodType.A_NEGATIVE,
                UserStatus.DONOR
        )
                .lastDonation(LocalDate.now().minusDays(14))
                .daysBetweenReminders(14)
                .nextReminder(LocalDate.now());

        testUserProfile = userProfileDao.saveNewUser(userProfileBuilder.build());

        UserCredentials userCredentialsEntityBilbo = new UserCredentials(passwordEncoder.encode(TEST_PASSWORD));

        userCredentialsDao.saveNewUserCredentials(testUserProfile.getUserId(), userCredentialsEntityBilbo);
    }

    @Override
    public void tearDown() {
        userProfileDao.deleteAll();
        userCredentialsDao.deleteAll();
    }

    @Test
    public void canGetUser() {
        given()
                .auth().basic(testUserProfile.getEmail(), TEST_PASSWORD)
        .expect()
                .statusCode(HttpStatus.SC_OK)
        .when()
                .get(BASE_PATH)
        .then()
                .assertThat()
                .body("name", equalTo(testUserProfile.getName()))
                .body("email", equalTo(testUserProfile.getEmail()))
                .body("bloodType", equalTo(testUserProfile.getBloodType().toString()))
                .body("lastDonation", equalTo(testUserProfile.getLastDonation().format(DATE_FORMATTER)))
                .body("daysBetweenReminders", equalTo(testUserProfile.getDaysBetweenReminders()))
                .body("nextReminder", equalTo(testUserProfile.getNextReminder().format(DATE_FORMATTER)))
                .body("userStatus", equalTo(testUserProfile.getUserStatus().toString()));
    }

    @Test
    public void canUpdateUser() {
        UpdateUserDto updateUserDto = userProfileToUpdateUserDto(testUserProfile);
        updateUserDto.setName("Bilbo Update");
        updateUserDto.setEmail("email@update.com");
        updateUserDto.setBloodType(BloodType.B_NEGATIVE); // Interesting to think if this should be allowed
        updateUserDto.setLastDonation(updateUserDto.getLastDonation().minusDays(1));
        updateUserDto.setDaysBetweenReminders(updateUserDto.getDaysBetweenReminders() - 1);
        updateUserDto.setNextReminder(updateUserDto.getNextReminder().minusDays(1));

        given().
                contentType(JSON_CONTENT_TYPE).
                body(updateUserDto).
                auth().basic(testUserProfile.getEmail(), TEST_PASSWORD).
        expect().
                statusCode(HttpStatus.SC_OK).
                when().
                put(BASE_PATH);

        given()
                .auth().basic(updateUserDto.getEmail(), TEST_PASSWORD)
        .expect()
                .statusCode(HttpStatus.SC_OK)
        .when()
                .get(BASE_PATH)
        .then()
                .assertThat()
                .body("name", equalTo(updateUserDto.getName()))
                .body("email", equalTo(updateUserDto.getEmail()))
                .body("bloodType", equalTo(updateUserDto.getBloodType().toString()))
                .body("lastDonation", equalTo(updateUserDto.getLastDonation().format(DATE_FORMATTER)))
                .body("daysBetweenReminders", equalTo(updateUserDto.getDaysBetweenReminders()))
                .body("nextReminder", equalTo(updateUserDto.getNextReminder().format(DATE_FORMATTER)))
                .body("userStatus", not(emptyString()));
    }

    @Test
    public void canUpdateUserPassword() {
        UpdateUserPasswordDto userPasswordDto = new UpdateUserPasswordDto();
        String newPassword = "passwordUpdate";
        userPasswordDto.setNewPassword(newPassword);
        given()
                .contentType(JSON_CONTENT_TYPE)
                .body(userPasswordDto)
                .auth().basic(testUserProfile.getEmail(), TEST_PASSWORD)
        .expect()
                .statusCode(HttpStatus.SC_NO_CONTENT)
        .when()
                .put(BASE_PATH + "/update-password");

        given()
                .auth().basic(testUserProfile.getEmail(), newPassword)
        .expect()
                .statusCode(HttpStatus.SC_OK)
        .when()
                .get(BASE_PATH);
    }

    @Test
    public void canCreateNewUser() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(1));
        newUserDto.setBloodType(BloodType.A_POSITIVE);
        newUserDto.setDaysBetweenReminders(90);
        newUserDto.setNextReminder(newUserDto.getLastDonation().plusDays(newUserDto.getDaysBetweenReminders()));

        given()
                .contentType(JSON_CONTENT_TYPE)
                .body(newUserDto)
        .expect()
                .statusCode(HttpStatus.SC_OK)
        .when()
                .post(BASE_PATH);

        given()
                .auth().basic(newUserDto.getEmail(), newUserDto.getPassword())
        .expect()
                .statusCode(HttpStatus.SC_OK)
        .when()
                .get(BASE_PATH)
        .then()
                .assertThat()
                .body("name", equalTo(newUserDto.getName()))
                .body("email", equalTo(newUserDto.getEmail()))
                .body("bloodType", equalTo(newUserDto.getBloodType().toString()))
                .body("lastDonation", equalTo(newUserDto.getLastDonation().format(DATE_FORMATTER)))
                .body("daysBetweenReminders", equalTo(newUserDto.getDaysBetweenReminders()))
                .body("nextReminder", equalTo(newUserDto.getNextReminder().format(DATE_FORMATTER)))
                .body("userStatus", not(emptyString()));
    }

    private UpdateUserDto userProfileToUpdateUserDto(UserProfile user) {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setName(user.getName());
        updateUserDto.setBloodType(user.getBloodType());
        updateUserDto.setLastDonation(user.getLastDonation());
        updateUserDto.setDaysBetweenReminders(user.getDaysBetweenReminders());
        updateUserDto.setNextReminder(user.getNextReminder());
        return updateUserDto;
    }
}
