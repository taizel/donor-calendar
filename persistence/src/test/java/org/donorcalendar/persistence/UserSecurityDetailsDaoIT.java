package org.donorcalendar.persistence;

import org.donorcalendar.AbstractPersistenceIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
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
public class UserSecurityDetailsDaoIT extends AbstractPersistenceIntegrationTest {

    private static final String TEST_PASSWORD = "test_password";
    private static final long TEST_ID = IdGenerator.generateNewId();

    @Autowired
    UserProfileDao userProfileDao;

    @Autowired
    UserSecurityDetailsDao target;

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
    public void saveNewUserSecurityDetails() {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(TEST_PASSWORD);

        UserSecurityDetails persistedUserSecurityDetails = target.saveNewUserSecurityDetails(TEST_ID, userSecurityDetails);

        assertEquals(userSecurityDetails.getPassword(), persistedUserSecurityDetails.getPassword());
    }

    @Test
    public void findByUserId() {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(TEST_PASSWORD);
        target.saveNewUserSecurityDetails(TEST_ID, userSecurityDetails);

        UserSecurityDetails persistedUserSecurityDetails = target.findByUserId(TEST_ID);

        assertEquals(userSecurityDetails.getPassword(), persistedUserSecurityDetails.getPassword());
    }

    @Test
    public void updateUserPassword() {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(TEST_PASSWORD);
        target.saveNewUserSecurityDetails(TEST_ID, userSecurityDetails);
        String newPassword = "password_update";

        target.updateUserPassword(TEST_ID, newPassword);

        assertEquals(newPassword, target.findByUserId(TEST_ID).getPassword());
    }
}
