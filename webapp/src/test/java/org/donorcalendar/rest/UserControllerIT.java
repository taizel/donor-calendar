package org.donorcalendar.rest;

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
import org.donorcalendar.rest.dto.UserResponseDto;
import org.donorcalendar.service.UserCredentialsService;
import org.donorcalendar.util.IdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerIT extends AbstractRestIntegrationTest {

    private static final String TEST_PASSWORD = "pass2";
    private static final String BASE_PATH = "/user";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(JacksonConfig.LOCAL_DATE_FORMAT);

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

    @AfterEach
    @Override
    public void tearDown() {
        userProfileDao.deleteAll();
        userCredentialsDao.deleteAll();
    }

    @Test
    void canGetUser() {
        client.get()
                .uri(BASE_PATH)
                .headers(h -> h.setBasicAuth(testUserProfile.getEmail(), TEST_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .value(response -> {
                    assertThat(response.getName())
                            .isEqualTo(testUserProfile.getName());
                    assertThat(response.getEmail())
                            .isEqualTo(testUserProfile.getEmail());
                    assertThat(response.getBloodType())
                            .isEqualTo(testUserProfile.getBloodType());
                    assertThat(response.getLastDonation().format(DATE_FORMATTER))
                            .isEqualTo(testUserProfile.getLastDonation().format(DATE_FORMATTER));
                    assertThat(response.getDaysBetweenReminders())
                            .isEqualTo(testUserProfile.getDaysBetweenReminders());
                    assertThat(response.getNextReminder().format(DATE_FORMATTER))
                            .isEqualTo(testUserProfile.getNextReminder().format(DATE_FORMATTER));
                    assertThat(response.getUserStatus())
                            .hasToString(testUserProfile.getUserStatus().toString());
                });
    }

    @Test
    void canUpdateUser() {
        UpdateUserDto updateUserDto = userProfileToUpdateUserDto(testUserProfile);
        updateUserDto.setName("Bilbo Update");
        updateUserDto.setEmail("email@update.com");
        updateUserDto.setBloodType(BloodType.B_NEGATIVE);
        updateUserDto.setLastDonation(updateUserDto.getLastDonation().minusDays(1));
        updateUserDto.setDaysBetweenReminders(updateUserDto.getDaysBetweenReminders() - 1);
        updateUserDto.setNextReminder(updateUserDto.getNextReminder().minusDays(1));

        // --- Perform update ---
        client.put()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBasicAuth(testUserProfile.getEmail(), TEST_PASSWORD))
                .bodyValue(updateUserDto)
                .exchange()
                .expectStatus().isOk();

        // --- Retrieve updated user ---
        client.get()
                .uri(BASE_PATH)
                .headers(h -> h.setBasicAuth(updateUserDto.getEmail(), TEST_PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .value(updated -> {
                    assertThat(updated.getName()).isEqualTo(updateUserDto.getName());
                    assertThat(updated.getEmail()).isEqualTo(updateUserDto.getEmail());
                    assertThat(updated.getBloodType()).isEqualTo(updateUserDto.getBloodType());
                    assertThat(updated.getLastDonation().format(DATE_FORMATTER))
                            .isEqualTo(updateUserDto.getLastDonation().format(DATE_FORMATTER));
                    assertThat(updated.getDaysBetweenReminders())
                            .isEqualTo(updateUserDto.getDaysBetweenReminders());
                    assertThat(updated.getNextReminder().format(DATE_FORMATTER))
                            .isEqualTo(updateUserDto.getNextReminder().format(DATE_FORMATTER));
                    assertThat(updated.getUserStatus().toString()).isNotEmpty();
                });
    }

    @Test
    void canUpdateUserPassword() {
        UpdateUserPasswordDto dto = new UpdateUserPasswordDto();
        String newPassword = "passwordUpdate";
        dto.setNewPassword(newPassword);

        // --- Update password ---
        client.put()
                .uri(BASE_PATH + "/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBasicAuth(testUserProfile.getEmail(), TEST_PASSWORD))
                .bodyValue(dto)
                .exchange()
                .expectStatus().isNoContent();

        // --- Authenticate with new password ---
        client.get()
                .uri(BASE_PATH)
                .headers(h -> h.setBasicAuth(testUserProfile.getEmail(), newPassword))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void canCreateNewUser() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("New");
        newUserDto.setEmail("new@newuser.com");
        newUserDto.setPassword("new");
        newUserDto.setLastDonation(LocalDate.now().minusMonths(1));
        newUserDto.setBloodType(BloodType.A_POSITIVE);
        newUserDto.setDaysBetweenReminders(90);
        newUserDto.setNextReminder(
                newUserDto.getLastDonation().plusDays(newUserDto.getDaysBetweenReminders())
        );

        // --- Create user ---
        client.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newUserDto)
                .exchange()
                .expectStatus().isOk();

        // --- Authenticate + retrieve ---
        client.get()
                .uri(BASE_PATH)
                .headers(h -> h.setBasicAuth(newUserDto.getEmail(), newUserDto.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponseDto.class)
                .value(created -> {
                    assertThat(created.getName()).isEqualTo(newUserDto.getName());
                    assertThat(created.getEmail()).isEqualTo(newUserDto.getEmail());
                    assertThat(created.getBloodType()).isEqualTo(newUserDto.getBloodType());
                    assertThat(created.getLastDonation().format(DATE_FORMATTER))
                            .isEqualTo(newUserDto.getLastDonation().format(DATE_FORMATTER));
                    assertThat(created.getDaysBetweenReminders())
                            .isEqualTo(newUserDto.getDaysBetweenReminders());
                    assertThat(created.getNextReminder().format(DATE_FORMATTER))
                            .isEqualTo(newUserDto.getNextReminder().format(DATE_FORMATTER));
                    assertThat(created.getUserStatus().toString()).isNotEmpty();
                });
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
