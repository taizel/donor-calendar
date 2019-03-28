package org.donorcalendar.service;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.ValidationException;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.util.IdGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

public class UserServiceTest {

    private static final String UNENCRYPTED_TEST_PASSWORD = "pass1";

    private UserProfileDao userProfileDao;

    private UserCredentialsService userCredentialsService;

    private UserService target;

    @Before
    public void setUp() {
        userProfileDao = Mockito.mock(UserProfileDao.class);
        userCredentialsService = Mockito.mock(UserCredentialsService.class);
        target = new UserService(userProfileDao, userCredentialsService);
    }

    @Test
    public void anyUserCreatesProfileAndSecurityDetails() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        UserProfile newUserAfterSuccess = new UserProfile(userProfileForTest);
        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        target.saveNewUser(userForTest);

        ArgumentCaptor<UserProfile> userProfileCaptor = ArgumentCaptor.forClass(UserProfile.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userProfileDao).saveNewUser(userProfileCaptor.capture());
        Mockito.verify(userCredentialsService).saveNewUserCredentials(userCaptor.capture());
        Assert.assertEquals(userProfileCaptor.getValue().getEmail(), userProfileForTest.getEmail());
        Assert.assertEquals(userCaptor.getValue().getUserSecurity(), userCredentialsForTest);
    }

    @Test
    public void newUserWithEmptyPassword() {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserCredentials userCredentialsForTest = new UserCredentials("");
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());

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
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        UserProfile newUserAfterSuccess = new UserProfile(userProfileForTest);
        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        target.saveNewUser(userForTest);

        ArgumentCaptor<UserProfile> userProfile = ArgumentCaptor.forClass(UserProfile.class);
        Mockito.verify(userProfileDao).saveNewUser(userProfile.capture());
        Assert.assertEquals(UserStatus.DONOR, userProfile.getValue().getUserStatus());
    }

    @Test
    public void saveUser_NewUserWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPast_SuccessWithStatusAsPotentialDonor() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(57));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        UserProfile newUserAfterSuccess = new UserProfile(userProfileForTest);
        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        target.saveNewUser(userForTest);

        ArgumentCaptor<UserProfile> userProfile = ArgumentCaptor.forClass(UserProfile.class);
        Mockito.verify(userProfileDao).saveNewUser(userProfile.capture());
        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, userProfile.getValue().getUserStatus());
    }

    @Test
    public void saveUser_NewUserWithLastDonationMoreThanHundredTwentyDays_SuccessWithStatusAsNeedToDonate() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(121));
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        UserProfile newUserAfterSuccess = new UserProfile(userProfileForTest);
        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        target.saveNewUser(userForTest);

        ArgumentCaptor<UserProfile> userProfile = ArgumentCaptor.forClass(UserProfile.class);
        Mockito.verify(userProfileDao).saveNewUser(userProfile.capture());
        Assert.assertEquals(UserStatus.NEED_TO_DONATE, userProfile.getValue().getUserStatus());
    }

    @Test
    public void saveUser_NewUserWithLastDonationNull_SuccessWithStatusAsNeedToDonate() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(null);
        UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userCredentialsForTest);
        UserProfile newUserAfterSuccess = new UserProfile(userProfileForTest);
        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        target.saveNewUser(userForTest);

        ArgumentCaptor<UserProfile> userProfile = ArgumentCaptor.forClass(UserProfile.class);
        Mockito.verify(userProfileDao).saveNewUser(userProfile.capture());
        Assert.assertEquals(UserStatus.NEED_TO_DONATE, userProfile.getValue().getUserStatus());
    }

    @Test
    public void saveUser_NewUserWithLastDonationInFuture_FailValidationLastDonationDateInFuture() {
        try {
            UserProfile userProfileForTest = createUserProfileForTest();
            userProfileForTest.setLastDonation(LocalDate.now().plusDays(2));
            UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
            User userForTest = new User(userProfileForTest, userCredentialsForTest);

            Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());

            target.saveNewUser(userForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Last donation date can't be in the future.", e.getMessage());
        }
    }

    @Test
    public void saveUser_NewUser_FailValidationEmailAlreadyTaken() {
        try {
            UserProfile userProfileForTest = createUserProfileForTest();
            UserCredentials userCredentialsForTest = new UserCredentials(UNENCRYPTED_TEST_PASSWORD);
            User userForTest = new User(userProfileForTest, userCredentialsForTest);

            Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.of(new UserProfile()));

            target.saveNewUser(userForTest);
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
            UserProfile userProfileForTest = createUserProfileForTestWithId();

            Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(false);

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
		UserProfile userProfileForTest = createUserProfileForTestWithId();
		userProfileForTest.setLastDonation(LocalDate.now().minusDays(56));
		Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

		target.updateUserProfile(userProfileForTest);

		ArgumentCaptor<UserProfile> userProfileCaptor = ArgumentCaptor.forClass(UserProfile.class);
		Mockito.verify(userProfileDao).updateUser(userProfileCaptor.capture());
		Assert.assertEquals(UserStatus.DONOR, userProfileCaptor.getValue().getUserStatus());
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPast_SuccessWithStatusAsPotentialDonor() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTestWithId();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(57));
        Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

        target.updateUserProfile(userProfileForTest);

		ArgumentCaptor<UserProfile> userProfileCaptor = ArgumentCaptor.forClass(UserProfile.class);
		Mockito.verify(userProfileDao).updateUser(userProfileCaptor.capture());
        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, userProfileCaptor.getValue().getUserStatus());
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationMoreThanHundredTwentyDays_SuccessWithStatusAsNeedToDonate() throws ValidationException, NotFoundException {
		UserProfile userProfileForTest = createUserProfileForTestWithId();
		userProfileForTest.setLastDonation(LocalDate.now().minusDays(121));
		Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

		target.updateUserProfile(userProfileForTest);

		ArgumentCaptor<UserProfile> userProfileCaptor = ArgumentCaptor.forClass(UserProfile.class);
		Mockito.verify(userProfileDao).updateUser(userProfileCaptor.capture());
		Assert.assertEquals(UserStatus.NEED_TO_DONATE, userProfileCaptor.getValue().getUserStatus());
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationNull_SuccessWithStatusAsNeedToDonate() throws ValidationException, NotFoundException {
		UserProfile userProfileForTest = createUserProfileForTestWithId();
		userProfileForTest.setLastDonation(null);
		Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

		target.updateUserProfile(userProfileForTest);

		ArgumentCaptor<UserProfile> userProfileCaptor = ArgumentCaptor.forClass(UserProfile.class);
		Mockito.verify(userProfileDao).updateUser(userProfileCaptor.capture());
		Assert.assertEquals(UserStatus.NEED_TO_DONATE, userProfileCaptor.getValue().getUserStatus());
    }

    @Test
    public void updateUserProfile_UserProfileLastDonationInFuture_FailValidationLastDonationDateInFuture() throws NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTestWithId();
        userProfileForTest.setLastDonation(LocalDate.now().plusDays(1));

        Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

        try {
            target.updateUserProfile(userProfileForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Last donation date can't be in the future.", e.getMessage());
        }
    }


    @Test
    public void updateUserPassword() throws NotFoundException, ValidationException {
        Long userId = IdGenerator.generateNewId();
        String unencryptedNewPassword = "newPassword";
        Mockito.when(userProfileDao.existsById(userId)).thenReturn(true);

        target.updateUserPassword(userId, unencryptedNewPassword);

        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> unencryptedNewPasswordCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(userCredentialsService).updateUserPassword(userIdCaptor.capture(), unencryptedNewPasswordCaptor.capture());
        Assert.assertEquals(userId, userIdCaptor.getValue());
        Assert.assertEquals(unencryptedNewPassword, unencryptedNewPasswordCaptor.getValue());
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

    private UserProfile createUserProfileForTestWithId() {
        UserProfile userProfile = createUserProfileForTest();
        userProfile.setUserId(IdGenerator.generateNewId());
        return userProfile;
    }
}
