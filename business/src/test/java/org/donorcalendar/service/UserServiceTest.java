package org.donorcalendar.service;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.User;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.model.ValidationException;
import org.donorcalendar.persistence.FakeUserCredentialsDao;
import org.donorcalendar.persistence.FakeUserProfileDao;
import org.donorcalendar.persistence.UserCredentialsDao;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.util.IdGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.donorcalendar.model.UserProfile.UserProfileBuilder;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserServiceTest {

    private static final String UNENCRYPTED_TEST_PASSWORD = "pass1";

    private final UserProfileDao userProfileDao = new FakeUserProfileDao();
    private final UserCredentialsDao userCredentialsDao = new FakeUserCredentialsDao();
    private final UserCredentialsService userCredentialsService = new UserCredentialsService(userCredentialsDao);

    private final UserService target = new UserService(userProfileDao, userCredentialsService);

    @Test
    public void anyUserCreatesProfileAndSecurityDetails() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertTrue(userProfileDao.findById(savedUserProfile.getUserId()).isPresent());
        Assert.assertNotNull(userCredentialsDao.findByUserId(savedUserProfile.getUserId()));
    }

    @Test
    public void newUserWithEmptyPassword() {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials("");
        User userForTest = new User(userProfileForTest, userCredentialsForTest);

        try {
            target.saveNewUser(userForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Password cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void newUserWithLastDonationUpToFiftySixDaysPastSuccessWithStatusAsDonor() throws ValidationException {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(LocalDate.now().minusDays(56));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(UserStatus.DONOR, savedUserProfile.getUserStatus());
    }

    @Test
    public void newUserWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPastSuccessWithStatusAsPotentialDonor() throws ValidationException {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(LocalDate.now().minusDays(57));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, savedUserProfile.getUserStatus());
    }

    @Test
    public void newUserWithLastDonationMoreThanHundredTwentyDaysSuccessWithStatusAsNeedToDonate() throws ValidationException {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(LocalDate.now().minusDays(121));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(UserStatus.NEED_TO_DONATE, savedUserProfile.getUserStatus());
    }

    @Test
    public void newUserWithLastDonationNullSuccessWithStatusAsNeedToDonate() throws ValidationException {
        UserProfileBuilder userProfileForTest = createUserProfileForTest();
        userProfileForTest.lastDonation(null);
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(UserStatus.NEED_TO_DONATE, savedUserProfile.getUserStatus());
    }

    @Test
    public void newUserWithLastDonationInFutureFailValidationLastDonationDateInFuture() {
        try {
            UserProfileBuilder userProfileForTest = createUserProfileForTest();
            userProfileForTest.lastDonation(LocalDate.now().plusDays(2));
            UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
            User userForTest = new User(userProfileForTest.build(), userCredentialsForTest);

            target.saveNewUser(userForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Last donation date can't be in the future.", e.getMessage());
        }
    }

    @Test
    public void newUserFailValidationEmailAlreadyTaken() throws ValidationException {
        UserProfile userProfileForTest1 = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest1 = new User(userProfileForTest1, userCredentialsForTest);
        target.saveNewUser(userForTest1);

        try {
            //Trying to save a user with the same email should fail
            target.saveNewUser(userForTest1);
            Assert.fail();
        } catch (ValidationException e) {
            assertThat(e.getMessage(), CoreMatchers.allOf(
                    CoreMatchers.containsString("The email "),
                    CoreMatchers.containsString("is already registered.")));
        }
    }

    @Test
    public void updateUserProfileFailUserNotFound() throws ValidationException {
        try {
            UserProfile userProfileForTest = createUserProfileForTest().build();

            target.updateUserProfile(userProfileForTest);
            Assert.fail();
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), CoreMatchers.allOf(
                    CoreMatchers.containsString("User with id "),
                    CoreMatchers.containsString(" could not be found.")));
        }
    }

    @Test
    public void updateUserProfileWithLastDonationUpToFiftySixDaysPastSuccessWithStatusAsDonor() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(LocalDate.now().minusDays(56))
                .build();

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        Assert.assertTrue(updatedUserProfile.isPresent());
        Assert.assertEquals(UserStatus.DONOR, updatedUserProfile.get().getUserStatus());
    }

    @Test
    public void updateUserProfileWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPastSuccessWithStatusAsPotentialDonor() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(LocalDate.now().minusDays(57))
                .build();

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        Assert.assertTrue(updatedUserProfile.isPresent());
        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, updatedUserProfile.get().getUserStatus());
    }

    @Test
    public void updateUserProfileWithLastDonationMoreThanHundredTwentyDaysSuccessWithStatusAsNeedToDonate() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(LocalDate.now().minusDays(121))
                .build();

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        Assert.assertTrue(updatedUserProfile.isPresent());
        Assert.assertEquals(UserStatus.NEED_TO_DONATE, updatedUserProfile.get().getUserStatus());
    }

    @Test
    public void updateUserProfileWithLastDonationNullSuccessWithStatusAsNeedToDonate() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(null).build();

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        Assert.assertTrue(updatedUserProfile.isPresent());
        Assert.assertEquals(UserStatus.NEED_TO_DONATE, updatedUserProfile.get().getUserStatus());
    }

    @Test
    public void updateUserProfileLastDonationInFutureFailValidationLastDonationDateInFuture()
            throws NotFoundException, ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest = new UserProfileBuilder(userProfileForTest).lastDonation(LocalDate.now().plusDays(1))
                .build();

        try {
            target.updateUserProfile(userProfileForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Last donation date can't be in the future.", e.getMessage());
        }
    }

    @Test
    public void updateUserPassword() throws NotFoundException, ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest().build();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        UserCredentials securityDetailsBeforeUpdate = userCredentialsDao.findByUserId(userProfileForTest.getUserId()).get();

        target.updateUserPassword(userProfileForTest.getUserId(), "differentPassword");

        UserCredentials securityDetailsAfterUpdate = userCredentialsDao.findByUserId(userProfileForTest.getUserId()).get();
        Assert.assertNotEquals(securityDetailsBeforeUpdate.getPassword(), securityDetailsAfterUpdate.getPassword());
    }

    @Test
    public void updateUserPasswordWithoutPassword() throws NotFoundException {
        try {
            target.updateUserPassword(IdGenerator.generateNewId(), null);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("New password cannot be empty.", e.getMessage());
        }
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
