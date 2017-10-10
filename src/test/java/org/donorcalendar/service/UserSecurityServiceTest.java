package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.donorcalendar.util.IdGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserSecurityServiceTest {

    private final String UNENCRYPTED_TEST_PASSWORD = "pass1";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserSecurityDetailsRepository userSecurityDetailsRepository;

    private UserSecurityService target;

    @Before
    public void setUp() {
        userSecurityDetailsRepository = Mockito.mock(UserSecurityDetailsRepository.class);
        target = new UserSecurityService(userSecurityDetailsRepository);
    }

    @Test
    public void saveNewUserSecurityDetails_ValidDetails_Success() {
        User user = createUserForTest();

        target.saveNewUserSecurityDetails(user);

        ArgumentCaptor<UserSecurityDetailsEntity> parameter = ArgumentCaptor.forClass(UserSecurityDetailsEntity.class);
        Mockito.verify(userSecurityDetailsRepository).save(parameter.capture());
        Assert.assertEquals(user.getUserProfile().getUserId(), parameter.getValue().getUserId());
        Assert.assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(user.getUserSecurity().getPassword(), parameter.getValue().getPassword()));
    }

    @Test
    public void updateUserPassword_ValidDetails_Success() {
        String newPassword = "updatedPassword";
        Long userId = IdGenerator.generateNewId();
        UserSecurityDetailsEntity userSecurityDetails = new UserSecurityDetailsEntity();
        userSecurityDetails.setUserId(userId);

        Mockito.when(userSecurityDetailsRepository.findByUserId(userId)).thenReturn(userSecurityDetails);

        target.updateUserPassword(userId, newPassword);

        ArgumentCaptor<UserSecurityDetailsEntity> parameter = ArgumentCaptor.forClass(UserSecurityDetailsEntity.class);
        Mockito.verify(userSecurityDetailsRepository).save(parameter.capture());
        Assert.assertEquals(userId, parameter.getValue().getUserId());
        Assert.assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(newPassword, parameter.getValue().getPassword()));
    }

    private User createUserForTest() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(IdGenerator.generateNewId());
        return new User(userProfile, new UserSecurityDetails(UNENCRYPTED_TEST_PASSWORD));
    }
}
