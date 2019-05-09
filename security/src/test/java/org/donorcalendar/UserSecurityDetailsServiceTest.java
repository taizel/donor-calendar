package org.donorcalendar;

import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.persistence.FakeUserProfileDao;
import org.donorcalendar.persistence.FakeUserCredentialsDao;
import org.donorcalendar.persistence.UserCredentialsDao;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.security.UserSecurityDetailsService;
import org.donorcalendar.util.IdGenerator;
import org.junit.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserSecurityDetailsServiceTest {
    private static final String TEST_EMAIL = "test@test.com";
    private static final Long TEST_ID = IdGenerator.generateNewId();

    private final UserProfileDao userProfileDao = new FakeUserProfileDao();
    private final UserCredentialsDao userCredentialsDao = new FakeUserCredentialsDao();
    private final UserSecurityDetailsService target = new UserSecurityDetailsService(userProfileDao, userCredentialsDao);

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsernameNoUserFound() {
        target.loadUserByUsername(TEST_EMAIL);
    }

    @Test(expected = BadCredentialsException.class)
    public void loadUserByUsernameNoCredentialsFound() {
        createUserProfile();
        target.loadUserByUsername(TEST_EMAIL);
    }

    @Test
    public void loadUserByUsernameSuccess() {
        createUserProfile();
        userCredentialsDao.saveNewUserCredentials(TEST_ID, new UserCredentials("password"));
        target.loadUserByUsername(TEST_EMAIL);
    }

    private void createUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(TEST_ID);
        userProfile.setEmail(TEST_EMAIL);
        userProfileDao.saveNewUser(userProfile);
    }
}
