package org.donorcalendar;

import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.persistence.FakeUserCredentialsDao;
import org.donorcalendar.persistence.FakeUserProfileDao;
import org.donorcalendar.persistence.UserCredentialsDao;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.security.UserSecurityDetailsService;
import org.donorcalendar.util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserSecurityDetailsServiceTest {
    private static final String TEST_EMAIL = "test@test.com";
    private static final Long TEST_ID = IdGenerator.generateNewId();

    private final UserProfileDao userProfileDao = new FakeUserProfileDao();
    private final UserCredentialsDao userCredentialsDao = new FakeUserCredentialsDao();
    private final UserSecurityDetailsService target =
            new UserSecurityDetailsService(userProfileDao, userCredentialsDao);

    @Test
    void loadUserByUsernameNoUserFound() {
        assertThatThrownBy(() -> target.loadUserByUsername(TEST_EMAIL))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loadUserByUsernameNoCredentialsFound() {
        createUserProfile();
        assertThatThrownBy(() -> target.loadUserByUsername(TEST_EMAIL))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loadUserByUsernameSuccess() {
        createUserProfile();
        userCredentialsDao.saveNewUserCredentials(TEST_ID, new UserCredentials("password"));

        UserDetails userDetails = target.loadUserByUsername(TEST_EMAIL);

        assertThat(userDetails.getUsername()).isEqualTo(TEST_EMAIL);
    }

    private void createUserProfile() {
        UserProfile userProfile = new UserProfile.UserProfileBuilder(
                TEST_ID,
                null,
                TEST_EMAIL,
                null,
                null
        ).build();
        userProfileDao.saveNewUser(userProfile);
    }
}
