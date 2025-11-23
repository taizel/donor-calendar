package org.donorcalendar.persistence;

import org.donorcalendar.AbstractPersistenceIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserCredentialsDaoIT extends AbstractPersistenceIntegrationTest {

    private static final String TEST_PASSWORD = "test_password";
    private static final long TEST_ID = IdGenerator.generateNewId();

    @Autowired
    UserProfileDao userProfileDao;

    @Autowired
    UserCredentialsDao target;

    @BeforeEach
    void setUp() {
        // Insert user profile to avoid FK violation
        UserProfile.UserProfileBuilder builder = new UserProfile.UserProfileBuilder(
                TEST_ID,
                "Test User",
                "test@test.com",
                BloodType.A_NEGATIVE,
                UserStatus.NEED_TO_DONATE
        );
        userProfileDao.saveNewUser(builder.build());
    }

    @Test
    void saveNewUserSecurityCredentials() {
        UserCredentials userCredentials = new UserCredentials(TEST_PASSWORD);

        UserCredentials persistedUserCredentials = target.saveNewUserCredentials(TEST_ID, userCredentials);

        assertThat(persistedUserCredentials.getPassword()).isEqualTo(userCredentials.getPassword());
    }

    @Test
    void findByUserId() {
        UserCredentials userCredentials = new UserCredentials(TEST_PASSWORD);
        target.saveNewUserCredentials(TEST_ID, userCredentials);

        UserCredentials persistedUserCredentials = target.findByUserId(TEST_ID).orElse(userCredentials);

        assertThat(persistedUserCredentials.getPassword()).isEqualTo(userCredentials.getPassword());
    }

    @Test
    void updateUserPassword() {
        UserCredentials userCredentials = new UserCredentials(TEST_PASSWORD);
        target.saveNewUserCredentials(TEST_ID, userCredentials);
        String newPassword = "password_update";

        target.saveUserPassword(TEST_ID, newPassword);

        assertThat(target.findByUserId(TEST_ID).orElse(userCredentials).getPassword()).isEqualTo(newPassword);
    }
}
