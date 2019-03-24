package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.persistence.UserSecurityDetailsDao;
import org.donorcalendar.util.IdGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserSecurityDetailsServiceTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserSecurityDetailsDao userSecurityDetailsDao;

    private UserSecurityDetailsServiceImpl target;

    @Before
    public void setUp() {
        userSecurityDetailsDao = Mockito.mock(UserSecurityDetailsDao.class);
        target = new UserSecurityDetailsServiceImpl(userSecurityDetailsDao);
    }

    @Test
    public void saveNewUserSecurityDetails_ValidDetails_Success() {
        User user = createUserForTest();
        ArgumentCaptor<Long> userIdParameter = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UserSecurityDetails> securityDetailsParameter = ArgumentCaptor.forClass(UserSecurityDetails.class);

        target.saveNewUserSecurityDetails(user);

        Mockito.verify(userSecurityDetailsDao).saveNewUserSecurityDetails(userIdParameter.capture(), securityDetailsParameter.capture());
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
        Mockito.when(userSecurityDetailsDao.findByUserId(userId)).thenReturn(userSecurityDetails);
        ArgumentCaptor<Long> userIdParameter = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> newPasswordParameter = ArgumentCaptor.forClass(String.class);

        target.updateUserPassword(userId, newPassword);

        Mockito.verify(userSecurityDetailsDao).updateUserPassword(userIdParameter.capture(), newPasswordParameter.capture());
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
