package org.donorcalendar.persistence;

import org.donorcalendar.AbstractPersistenceIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.util.IdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
@SpringBootTest
public class UserCredentialsDaoIT extends AbstractPersistenceIntegrationTest {

    private static final String TEST_PASSWORD = "test_password";
    private static final long TEST_ID = IdGenerator.generateNewId();

    @Autowired
    UserProfileDao userProfileDao;

    @Autowired
	UserCredentialsDao target;

    @Before
    public void setUp() {
        // Insert user profile to avoid FK violation
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(TEST_ID);
        userProfile.setName("Test User");
        userProfile.setEmail("test@test.com");
        userProfile.setBloodType(BloodType.A_NEGATIVE);
        userProfile.setUserStatus(UserStatus.NEED_TO_DONATE);
        userProfileDao.saveNewUser(userProfile);
    }

    @Test
    public void saveNewUserSecurityCredentials() {
        UserCredentials userCredentials = new UserCredentials(TEST_PASSWORD);

        UserCredentials persistedUserCredentials = target.saveNewUserCredentials(TEST_ID, userCredentials);

        assertEquals(userCredentials.getPassword(), persistedUserCredentials.getPassword());
    }

    @Test
    public void findByUserId() {
        UserCredentials userCredentials = new UserCredentials(TEST_PASSWORD);
        target.saveNewUserCredentials(TEST_ID, userCredentials);

        UserCredentials persistedUserCredentials = target.findByUserId(TEST_ID);

        assertEquals(userCredentials.getPassword(), persistedUserCredentials.getPassword());
    }

    @Test
    public void updateUserPassword() {
        UserCredentials userCredentials = new UserCredentials(TEST_PASSWORD);
        target.saveNewUserCredentials(TEST_ID, userCredentials);
        String newPassword = "password_update";

        target.updateUserPassword(TEST_ID, newPassword);

        assertEquals(newPassword, target.findByUserId(TEST_ID).getPassword());
    }
}
