package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.persistence.UserSecurityDao;
import org.donorcalendar.util.IdGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserSecurityServiceTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserSecurityDao userSecurityDao;

    private UserSecurityService target;

    @Before
    public void setUp() {
        userSecurityDao = Mockito.mock(UserSecurityDao.class);
        target = new UserSecurityService(userSecurityDao);
    }

    @Test
    public void saveNewUserSecurityDetails_ValidDetails_Success() {
        User user = createUserForTest();
        ArgumentCaptor<Long> userIdParameter = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UserSecurityDetails> securityDetailsParameter = ArgumentCaptor.forClass(UserSecurityDetails.class);

        target.saveNewUserSecurityDetails(user);

        Mockito.verify(userSecurityDao).saveNewUserSecurityDetails(userIdParameter.capture(), securityDetailsParameter.capture());
        Assert.assertEquals(user.getUserProfile().getUserId(), userIdParameter.getValue());
        Assert.assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(user.getUserSecurity().getPassword(), securityDetailsParameter.getValue().getPassword()));
    }

    @Test
    public void updateUserPassword_ValidDetails_Success() {
        String oldPassword = "oldPassword";
        String newPassword = "updatedPassword";
        Long userId = IdGenerator.generateNewId();
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(oldPassword);
        Mockito.when(userSecurityDao.findByUserId(userId)).thenReturn(userSecurityDetails);
        ArgumentCaptor<Long> userIdParameter = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> newPasswordParameter = ArgumentCaptor.forClass(String.class);

        target.updateUserPassword(userId, newPassword);

        Mockito.verify(userSecurityDao).updateUserPassword(userIdParameter.capture(), newPasswordParameter.capture());
        Assert.assertEquals(userId, userIdParameter.getValue());
        Assert.assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(newPassword, newPasswordParameter.getValue()));
    }

    private User createUserForTest() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(IdGenerator.generateNewId());
        return new User(userProfile, new UserSecurityDetails("password" + userProfile.getUserId()));
    }
}
