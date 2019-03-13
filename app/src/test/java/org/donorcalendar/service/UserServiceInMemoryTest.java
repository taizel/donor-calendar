package org.donorcalendar.service;

import java.time.LocalDate;
import java.util.Optional;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.model.ValidationException;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.persistence.UserProfileDaoInMemoryImpl;
import org.donorcalendar.persistence.UserSecurityDetailsDao;
import org.donorcalendar.persistence.UserSecurityDetailsDaoInMemoryImpl;
import org.donorcalendar.util.IdGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class UserServiceInMemoryTest {

    private static final String UNENCRYPTED_TEST_PASSWORD = "pass1";

    private final UserProfileDao userProfileDao = new UserProfileDaoInMemoryImpl();
    private final UserSecurityDetailsDao userSecurityDetailsDao = new UserSecurityDetailsDaoInMemoryImpl();
    private final UserSecurityService userSecurityService = new UserSecurityService(userSecurityDetailsDao);

    private final UserService target = new UserService(userProfileDao, userSecurityService);

    @Test
    public void anyUserCreatesProfileAndSecurityDetails() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertTrue(userProfileDao.findById(savedUserProfile.getUserId()).isPresent());
        Assert.assertNotNull(userSecurityDetailsDao.findByUserId(savedUserProfile.getUserId()));
    }

    @Test
    public void newUserWithEmptyPassword() {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails("");
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        try {
            target.saveNewUser(userForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Password cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void saveUser_NewUserWithLastDonationUpToFiftySixDaysPast_SuccessWithStatusAsDonor() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(56));
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(UserStatus.DONOR, savedUserProfile.getUserStatus());
    }

    @Test
    public void saveUser_NewUserWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPast_SuccessWithStatusAsPotentialDonor() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(57));
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, savedUserProfile.getUserStatus());
    }

    @Test
    public void saveUser_NewUserWithLastDonationMoreThanHundredTwentyDays_SuccessWithStatusAsNeedToDonate() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(121));
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(UserStatus.NEED_TO_DONATE, savedUserProfile.getUserStatus());
    }

    @Test
    public void saveUser_NewUserWithLastDonationNull_SuccessWithStatusAsNeedToDonate() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(null);
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(UserStatus.NEED_TO_DONATE, savedUserProfile.getUserStatus());
    }

    @Test
    public void saveUser_NewUserWithLastDonationInFuture_FailValidationLastDonationDateInFuture() {
        try {
            UserProfile userProfileForTest = createUserProfileForTest();
            userProfileForTest.setLastDonation(LocalDate.now().plusDays(2));
            UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
            User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

            target.saveNewUser(userForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Last donation date can't be in the future.", e.getMessage());
        }
    }

    @Test
    public void saveUser_NewUser_FailValidationEmailAlreadyTaken() {
        try {
            UserProfile userProfileForTest1 = createUserProfileForTest();
            UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
            User userForTest1 = new User(userProfileForTest1, userSecurityDetailsForTest);
            target.saveNewUser(userForTest1);

            //Trying to save a user with the same email should fail
            target.saveNewUser(userForTest1);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(
                    CoreMatchers.containsString("The email "),
                    CoreMatchers.containsString("is already registered.")));
        }
    }

    @Test
    public void updateUserProfile_UserProfileWithInvalidId_FailUserNotFound() throws ValidationException {
        try {
            UserProfile userProfileForTest = createUserProfileForTest();

            target.updateUserProfile(userProfileForTest);
            Assert.fail();
        } catch (NotFoundException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.allOf(
                    CoreMatchers.containsString("User with id "),
                    CoreMatchers.containsString(" could not be found.")));
        }
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationUpToFiftySixDaysPast_SuccessWithStatusAsDonor() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(56));

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        Assert.assertTrue(updatedUserProfile.isPresent());
        Assert.assertEquals(UserStatus.DONOR, updatedUserProfile.get().getUserStatus());
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPast_SuccessWithStatusAsPotentialDonor() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(57));

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        Assert.assertTrue(updatedUserProfile.isPresent());
        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, updatedUserProfile.get().getUserStatus());
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationMoreThanHundredTwentyDays_SuccessWithStatusAsNeedToDonate() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(121));

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        Assert.assertTrue(updatedUserProfile.isPresent());
        Assert.assertEquals(UserStatus.NEED_TO_DONATE, updatedUserProfile.get().getUserStatus());
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationNull_SuccessWithStatusAsNeedToDonate() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest.setLastDonation(null);

        target.updateUserProfile(userProfileForTest);

        Optional<UserProfile> updatedUserProfile = userProfileDao.findById(userProfileForTest.getUserId());
        Assert.assertTrue(updatedUserProfile.isPresent());
        Assert.assertEquals(UserStatus.NEED_TO_DONATE, updatedUserProfile.get().getUserStatus());
    }

    @Test
    public void updateUserProfile_UserProfileLastDonationInFuture_FailValidationLastDonationDateInFuture()
            throws NotFoundException, ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        userProfileForTest.setLastDonation(LocalDate.now().plusDays(1));

        try {
            target.updateUserProfile(userProfileForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Last donation date can't be in the future.", e.getMessage());
        }
    }

    @Test
    public void updateUserPassword() throws NotFoundException, ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);
        userProfileForTest = target.saveNewUser(userForTest);
        UserSecurityDetails securityDetailsBeforeUpdate = userSecurityDetailsDao.findByUserId(userProfileForTest.getUserId());

        target.updateUserPassword(userProfileForTest.getUserId(), "differentPassword");

        UserSecurityDetails securityDetailsAfterUpdate = userSecurityDetailsDao.findByUserId(userProfileForTest.getUserId());
        Assert.assertNotEquals(securityDetailsBeforeUpdate.getPassword(), securityDetailsAfterUpdate.getPassword());
    }

    @Test
    public void updateUserPasswordWithoutPassword() throws NotFoundException {
        try{
            target.updateUserPassword(IdGenerator.generateNewId(), null);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("New password cannot be empty.", e.getMessage());
        }
    }

    private UserProfile createUserProfileForTest() {
        UserProfile userProfile = new UserProfile();
        long id = IdGenerator.generateNewId();
        userProfile.setName("John Doe " + id);
        userProfile.setEmail(id + "johntest@test.com");
        userProfile.setLastDonation(LocalDate.now().minusDays(7));
        userProfile.setBloodType(BloodType.AB_NEGATIVE);
        userProfile.setDaysBetweenReminders(30);
        userProfile.setNextReminder(LocalDate.now().plusDays(60));
        return userProfile;
    }
}
