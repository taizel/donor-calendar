package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.persistence.UserCredentialsDao;
import org.donorcalendar.util.IdGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserCredentialsServiceTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserCredentialsDao userCredentialsDao;

    private UserCredentialsService target;

    @Before
    public void setUp() {
        userCredentialsDao = Mockito.mock(UserCredentialsDao.class);
        target = new UserCredentialsService(userCredentialsDao);
    }

    @Test
    public void saveNewUserCredentials_ValidCredentials_Success() {
        User user = createUserForTest();
        ArgumentCaptor<Long> userIdParameter = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UserCredentials> securityDetailsParameter = ArgumentCaptor.forClass(UserCredentials.class);

        target.saveNewUserCredentials(user);

        Mockito.verify(userCredentialsDao).saveNewUserCredentials(userIdParameter.capture(), securityDetailsParameter.capture());
        Assert.assertEquals(user.getUserProfile().getUserId(), userIdParameter.getValue());
        Assert.assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(user.getUserSecurity().getPassword(), securityDetailsParameter.getValue().getPassword()));
    }

    @Test
    public void updateUserPassword_ValidCredentials_Success() {
        String oldPassword = "oldPassword";
        String newPassword = "updatedPassword";
        Long userId = IdGenerator.generateNewId();
        UserCredentials userCredentials = new UserCredentials(oldPassword);
        Mockito.when(userCredentialsDao.findByUserId(userId)).thenReturn(userCredentials);
        ArgumentCaptor<Long> userIdParameter = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> newPasswordParameter = ArgumentCaptor.forClass(String.class);

        target.updateUserPassword(userId, newPassword);

        Mockito.verify(userCredentialsDao).updateUserPassword(userIdParameter.capture(), newPasswordParameter.capture());
        Assert.assertEquals(userId, userIdParameter.getValue());
        Assert.assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(newPassword, newPasswordParameter.getValue()));
    }

    private User createUserForTest() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(IdGenerator.generateNewId());
        return new User(userProfile, new UserCredentials("password" + userProfile.getUserId()));
    }
}
