package org.donorcalendar.persistence;

import org.donorcalendar.AbstractIntegrationTest;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.util.IdGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
@SpringBootTest
public class UserSecurityDetailsDaoIT extends AbstractIntegrationTest {

    private static final String TEST_PASSWORD = "test_password";
    private static final long TEST_ID = IdGenerator.generateNewId();

    @Autowired
    UserSecurityDetailsDao userSecurityDetailsDao;

    @Test
    public void saveNewUserSecurityDetails() {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(TEST_PASSWORD);

        UserSecurityDetails persistedUserSecurityDetails = userSecurityDetailsDao.saveNewUserSecurityDetails(TEST_ID, userSecurityDetails);

        assertEquals(userSecurityDetails.getPassword(), persistedUserSecurityDetails.getPassword());
    }

    @Test
    public void findByUserId() {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(TEST_PASSWORD);
        userSecurityDetailsDao.saveNewUserSecurityDetails(TEST_ID, userSecurityDetails);

        UserSecurityDetails persistedUserSecurityDetails = userSecurityDetailsDao.findByUserId(TEST_ID);

        assertEquals(userSecurityDetails.getPassword(), persistedUserSecurityDetails.getPassword());
    }

    @Test
    public void updateUserPassword() {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(TEST_PASSWORD);
        userSecurityDetailsDao.saveNewUserSecurityDetails(TEST_ID, userSecurityDetails);
        String newPassword = "password_update";

        userSecurityDetailsDao.updateUserPassword(TEST_ID, newPassword);

        assertEquals(newPassword, userSecurityDetailsDao.findByUserId(TEST_ID).getPassword());
    }
}
