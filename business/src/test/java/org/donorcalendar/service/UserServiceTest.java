package org.donorcalendar.service;

import org.donorcalendar.model.*;
import org.donorcalendar.persistence.FakeUserCredentialsDao;
import org.donorcalendar.persistence.FakeUserProfileDao;
import org.donorcalendar.persistence.UserCredentialsDao;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.util.IdGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.donorcalendar.model.UserProfile.UserProfileBuilder;

class UserServiceTest {

    private static final String UNENCRYPTED_TEST_PASSWORD = "pass1";

    private final UserProfileDao userProfileDao = new FakeUserProfileDao();
    private final UserCredentialsDao userCredentialsDao = new FakeUserCredentialsDao();
    private final UserCredentialsService userCredentialsService = new UserCredentialsService(userCredentialsDao);

    private final UserService target = new UserService(userProfileDao, userCredentialsService);

    @Test
    void anyUserCreatesProfileAndSecurityDetails() throws Exception {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        assertThat(userProfileDao.findById(savedUserProfile.getUserId())).isPresent();
        assertThat(userCredentialsDao.findByUserId(savedUserProfile.getUserId())).isNotNull();
    }

    @Test
    void newUserWithEmptyPassword() {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials("");
        User userForTest = new User(userProfileForTest, userCredentialsForTest);

        assertThatThrownBy(() -> target.saveNewUser(userForTest))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Password cannot be empty.");
    }

    @Test
    void newUserWithLastDonationUpToFiftySixDaysPastSuccessWithStatusAsDonor() throws Exception {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(LocalDate.now().minusDays(56));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        assertThat(savedUserProfile.getUserStatus()).isEqualTo(UserStatus.DONOR);
    }

    @Test
    void newUserWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPastSuccessWithStatusAsPotentialDonor() throws Exception {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(LocalDate.now().minusDays(57));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        assertThat(savedUserProfile.getUserStatus()).isEqualTo(UserStatus.POTENTIAL_DONOR);
    }

    @Test
    void newUserWithLastDonationMoreThanHundredTwentyDaysSuccessWithStatusAsNeedToDonate() throws Exception {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(LocalDate.now().minusDays(121));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        assertThat(savedUserProfile.getUserStatus()).isEqualTo(UserStatus.NEED_TO_DONATE);
    }

    @Test
    void newUserWithLastDonationNullSuccessWithStatusAsNeedToDonate() throws Exception {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(null);
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        assertThat(savedUserProfile.getUserStatus()).isEqualTo(UserStatus.NEED_TO_DONATE);
    }

    @Test
    void newUserWithLastDonationInFutureFailValidationLastDonationDateInFuture() {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(LocalDate.now().plusDays(2));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        assertThatThrownBy(() -> target.saveNewUser(userForTest))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Last donation date can't be in the future.");
    }

    @Test
    void newUserFailValidationEmailAlreadyTaken() throws ValidationException {
        UserProfile userProfileForTest1 = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest1 = new User(userProfileForTest1, userCredentialsForTest);
        target.saveNewUser(userForTest1);

        // Trying to save a user with the same email should fail
        assertThatThrownBy(() -> target.saveNewUser(userForTest1))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("The email ")
                .hasMessageContaining("is already registered.");
    }

    @Test
    void updateUserProfileFailUserNotFound() {
        UserProfile userProfileForTest = createUserProfileForTest().build();

        assertThatThrownBy(() -> target.updateUserProfile(userProfileForTest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with id ")
                .hasMessageContaining(" could not be found.");
    }

    @Test
    void updateUserProfileWithLastDonationUpToFiftySixDaysPastSuccessWithStatusAsDonor() throws Exception {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(LocalDate.now().minusDays(56))
                .build();

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        assertThat(updatedUserProfile).isPresent();
        assertThat(updatedUserProfile.get().getUserStatus()).isEqualTo(UserStatus.DONOR);
    }

    @Test
    void updateUserProfileWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPastSuccessWithStatusAsPotentialDonor() throws Exception {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(LocalDate.now().minusDays(57))
                .build();

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        assertThat(updatedUserProfile).isPresent();
        assertThat(updatedUserProfile.get().getUserStatus()).isEqualTo(UserStatus.POTENTIAL_DONOR);
    }

    @Test
    void updateUserProfileWithLastDonationMoreThanHundredTwentyDaysSuccessWithStatusAsNeedToDonate() throws Exception {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(LocalDate.now().minusDays(121))
                .build();

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        assertThat(updatedUserProfile).isPresent();
        assertThat(updatedUserProfile.get().getUserStatus()).isEqualTo(UserStatus.NEED_TO_DONATE);
    }

    @Test
    void updateUserProfileWithLastDonationNullSuccessWithStatusAsNeedToDonate() throws Exception {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(null).build();

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        assertThat(updatedUserProfile).isPresent();
        assertThat(updatedUserProfile.get().getUserStatus()).isEqualTo(UserStatus.NEED_TO_DONATE);
    }

    @Test
    void updateUserProfileLastDonationInFutureFailValidationLastDonationDateInFuture()
            throws Exception {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(LocalDate.now().plusDays(1))
                .build();
        final var updateRequest = userProfileForTest;

        assertThatThrownBy(() -> target.updateUserProfile(updateRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Last donation date can't be in the future.");
    }

    @Test
    void updateUserPassword() throws Exception {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        UserCredentials securityDetailsBeforeUpdate = userCredentialsDao.findByUserId(userProfileForTest.getUserId()).get();

        target.updateUserPassword(userProfileForTest.getUserId(), "differentPassword");

        UserCredentials securityDetailsAfterUpdate = userCredentialsDao.findByUserId(userProfileForTest.getUserId()).get();
        assertThat(securityDetailsAfterUpdate.getPassword()).isNotEqualTo(securityDetailsBeforeUpdate.getPassword());
    }

    @Test
    void updateUserPasswordWithoutPassword() {
        assertThatThrownBy(() -> target.updateUserPassword(IdGenerator.generateNewId(), null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("New password cannot be empty.");
    }

    private UserProfileBuilder createUserProfileForTest() {
        long id = IdGenerator.generateNewId();
        UserProfileBuilder userProfileBuilder = new UserProfileBuilder(0,
                "John Doe " + id,
                id + "johntest@test.com",
                BloodType.AB_NEGATIVE,
                null);
        userProfileBuilder.lastDonation(LocalDate.now().minusDays(7));
        userProfileBuilder.daysBetweenReminders(30);
        userProfileBuilder.nextReminder(LocalDate.now().plusDays(60));
        return userProfileBuilder;
    }
}
