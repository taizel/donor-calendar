package org.donorcalendar.service;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.ValidationException;
import org.donorcalendar.persistence.UserProfileDao;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

public class UserServiceTest {

    private final String UNENCRYPTED_TEST_PASSWORD = "pass1";

    private UserProfileDao userProfileDao;

    private UserSecurityService userSecurityService;

    private UserService target;

    @Before
    public void setUp() {
        userProfileDao = Mockito.mock(UserProfileDao.class);
        userSecurityService = Mockito.mock(UserSecurityService.class);
        target = new UserService(userProfileDao, userSecurityService);
    }

    @Test
    public void anyUserCreatesProfileAndSecurityDetails() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile newUserAfterSuccess = createUserProfileForTest();
        newUserAfterSuccess.setUserId(1L);

        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(newUserAfterSuccess.getUserId(), savedUserProfile.getUserId());
        Mockito.verify(userSecurityService).saveNewUserSecurityDetails(userForTest);
    }

    @Test
    public void saveUser_NewUserWithLastDonationUpToFiftySixDaysPast_SuccessWithStatusAsDonor() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(56));
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile newUserAfterSuccess = createUserProfileForTest();
        newUserAfterSuccess.setUserId(1L);

        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(newUserAfterSuccess.getUserId(), savedUserProfile.getUserId());
        Assert.assertEquals(UserStatus.DONOR, userProfileForTest.getUserStatus());
        Mockito.verify(userSecurityService).saveNewUserSecurityDetails(userForTest);
    }

    @Test
    public void saveUser_NewUserWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPast_SuccessWithStatusAsPotentialDonor() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(57));
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile newUserAfterSuccess = createUserProfileForTest();
        newUserAfterSuccess.setUserId(1L);

        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(newUserAfterSuccess.getUserId(), savedUserProfile.getUserId());
        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, userProfileForTest.getUserStatus());
        Mockito.verify(userSecurityService).saveNewUserSecurityDetails(userForTest);
    }

    @Test
    public void saveUser_NewUserWithLastDonationMoreThanHundredTwentyDays_SuccessWithStatusAsNeedToDonate() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(121));
        userProfileForTest.setUserStatus(null);
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile newUserAfterSuccess = createUserProfileForTest();
        newUserAfterSuccess.setUserId(1L);

        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(newUserAfterSuccess.getUserId(), savedUserProfile.getUserId());
        Assert.assertEquals(UserStatus.NEED_TO_DONATE, userProfileForTest.getUserStatus());
        Mockito.verify(userSecurityService).saveNewUserSecurityDetails(userForTest);
    }

    @Test
    public void saveUser_NewUserWithLastDonationNull_SuccessWithStatusAsNeedToDonate() throws ValidationException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(null);
        userProfileForTest.setUserStatus(null);
        UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
        User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

        UserProfile newUserAfterSuccess = createUserProfileForTest();
        newUserAfterSuccess.setUserId(1L);

        Mockito.when(userProfileDao.findByEmail(userProfileForTest.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userProfileDao.saveNewUser(userProfileForTest)).thenReturn(newUserAfterSuccess);

        UserProfile savedUserProfile = target.saveNewUser(userForTest);

        Assert.assertEquals(newUserAfterSuccess.getUserId(), savedUserProfile.getUserId());
        Assert.assertEquals(UserStatus.NEED_TO_DONATE, userProfileForTest.getUserStatus());
        Mockito.verify(userSecurityService).saveNewUserSecurityDetails(userForTest);
    }

    @Test
    public void saveUser_NewUserWithLastDonationInFuture_FailValidationLastDonationDateInFuture() {
        try {
            UserProfile userProfileForTest = createUserProfileForTest();
            userProfileForTest.setLastDonation(LocalDate.now().plusDays(2));
            UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
            User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

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
            UserSecurityDetails userSecurityDetailsForTest = new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD);
            User userForTest = new User(userProfileForTest, userSecurityDetailsForTest);

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
            UserProfile userProfileForTest = createUserProfileForTest();

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
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(56));
        userProfileForTest.setUserStatus(null);

        Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

        target.updateUserProfile(userProfileForTest);

        Assert.assertEquals(UserStatus.DONOR, userProfileForTest.getUserStatus());
        Mockito.verify(userProfileDao).updateUser(userProfileForTest);
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationMoreThanFiftySixAndUpToHundredTwentyDaysPast_SuccessWithStatusAsPotentialDonor() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(57));
        userProfileForTest.setUserStatus(null);

        Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

        target.updateUserProfile(userProfileForTest);

        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, userProfileForTest.getUserStatus());
        Mockito.verify(userProfileDao).updateUser(userProfileForTest);
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationMoreThanHundredTwentyDays_SuccessWithStatusAsNeedToDonate() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().minusDays(121));
        userProfileForTest.setUserStatus(null);

        Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

        target.updateUserProfile(userProfileForTest);

        Assert.assertEquals(UserStatus.NEED_TO_DONATE, userProfileForTest.getUserStatus());
        Mockito.verify(userProfileDao).updateUser(userProfileForTest);
    }

    @Test
    public void updateUserProfile_UserProfileWithLastDonationNull_SuccessWithStatusAsNeedToDonate() throws ValidationException, NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(null);
        userProfileForTest.setUserStatus(null);

        Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

        target.updateUserProfile(userProfileForTest);

        Assert.assertEquals(UserStatus.NEED_TO_DONATE, userProfileForTest.getUserStatus());
        Mockito.verify(userProfileDao).updateUser(userProfileForTest);
    }

    @Test
    public void updateUserProfile_UserProfileLastDonationInFuture_FailValidationLastDonationDateInFuture() throws NotFoundException {
        UserProfile userProfileForTest = createUserProfileForTest();
        userProfileForTest.setLastDonation(LocalDate.now().plusDays(1));

        Mockito.when(userProfileDao.existsById(userProfileForTest.getUserId())).thenReturn(true);

        try {
            target.updateUserProfile(userProfileForTest);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Last donation date can't be in the future.", e.getMessage());
        }
    }

    private UserProfile createUserProfileForTest() {
        UserProfile userProfile = new UserProfile();
        userProfile.setName("John Test");
        userProfile.setEmail("johntest@test.com");
        userProfile.setLastDonation(LocalDate.now().minusDays(7));
        userProfile.setBloodType(BloodType.AB_NEGATIVE);
        userProfile.setDaysBetweenReminders(30);
        userProfile.setNextReminder(LocalDate.now().plusDays(60));
        return userProfile;
    }
}
