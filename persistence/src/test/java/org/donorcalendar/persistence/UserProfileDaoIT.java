package org.donorcalendar.persistence;

import org.donorcalendar.AbstractPersistenceIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.util.IdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
@SpringBootTest
public class UserProfileDaoIT extends AbstractPersistenceIntegrationTest {

    @Autowired
    private UserProfileDao userProfileDao;

    private static final LocalDate TODAY = LocalDate.now();

    @Test
    public void saveNewUser() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        UserProfile persistedUser = userProfileDao.saveNewUser(userToPersist);

        assertUserProfileFieldsMatches(userToPersist, persistedUser);
    }

    @Test
    public void findAll() {
        UserProfile userToPersist1 = generateDefaultTestUserProfile();
        UserProfile userToPersist2 = generateDefaultTestUserProfile();
        userProfileDao.saveNewUser(userToPersist1);
        userProfileDao.saveNewUser(userToPersist2);

        List<UserProfile> allPersistedUsers = userProfileDao.findAll();

        Assert.assertEquals(2, allPersistedUsers.size());
        for (UserProfile user : allPersistedUsers) {
            if (userToPersist1.getUserId().equals(user.getUserId())) {
                assertUserProfileFieldsMatches(userToPersist1, user);
            } else if (userToPersist2.getUserId().equals(user.getUserId())) {
                assertUserProfileFieldsMatches(userToPersist2, user);
            } else {
                Assert.fail();
            }
        }
    }

    @Test
    public void findById() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        userProfileDao.saveNewUser(userToPersist);

        UserProfile persistedUser = userProfileDao.findById(userToPersist.getUserId()).orElse(new UserProfile());

        assertUserProfileFieldsMatches(userToPersist, persistedUser);
    }

    @Test
    public void findByEmail() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        userProfileDao.saveNewUser(userToPersist);

        UserProfile persistedUser = userProfileDao.findByEmail(userToPersist.getEmail()).orElse(new UserProfile());

        assertUserProfileFieldsMatches(userToPersist, persistedUser);
    }

    @Test
    public void exists() {
        UserProfile userToPersist = generateDefaultTestUserProfile();

        boolean beforeSave = userProfileDao.existsById(userToPersist.getUserId());
        userProfileDao.saveNewUser(userToPersist);
        boolean afterSave = userProfileDao.existsById(userToPersist.getUserId());

        Assert.assertFalse(beforeSave);
        Assert.assertTrue(afterSave);
    }

    @Test
    public void updateUser() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        userToPersist.setBloodType(BloodType.O_POSITIVE);
        userToPersist.setUserStatus(UserStatus.NEED_TO_DONATE);
        UserProfile userToUpdate = userProfileDao.saveNewUser(userToPersist);
        userToUpdate.setName("Updated " + userToUpdate.getName());
        userToUpdate.setEmail("update" + userToUpdate.getEmail());
        userToUpdate.setLastDonation(userToUpdate.getLastDonation().minusDays(1));
        userToUpdate.setBloodType(BloodType.AB_NEGATIVE);
        userToUpdate.setDaysBetweenReminders(userToUpdate.getDaysBetweenReminders() + 30);
        userToUpdate.setNextReminder(userToUpdate.getNextReminder().plusDays(90));
        userToUpdate.setUserStatus(UserStatus.DONOR);

        userProfileDao.updateUser(userToUpdate);

        UserProfile updatedUserProfile = userProfileDao.findById(userToUpdate.getUserId()).orElse(new UserProfile());
        assertUserProfileFieldsMatches(userToUpdate, updatedUserProfile);
    }

    @Test
    public void findUsersToRemind() {
        UserProfile userToRemind1 = generateDefaultTestUserProfile();
        userToRemind1.setNextReminder(TODAY);
        UserProfile userToRemind2 = generateDefaultTestUserProfile();
        userToRemind2.setNextReminder(TODAY.minusDays(1));
        UserProfile userToIgnore1 = generateDefaultTestUserProfile();
        userToIgnore1.setNextReminder(TODAY.plusDays(1));
        UserProfile userToIgnore2 = generateDefaultTestUserProfile();
        userToIgnore1.setNextReminder(null);
        userProfileDao.saveNewUser(userToRemind1);
        userProfileDao.saveNewUser(userToRemind2);
        userProfileDao.saveNewUser(userToIgnore1);
        userProfileDao.saveNewUser(userToIgnore2);

        List<UserProfile> allPersistedUsers = userProfileDao.findUsersToRemind();

        Assert.assertEquals(2, allPersistedUsers.size());
        for (UserProfile user : allPersistedUsers) {
            if (userToRemind1.getUserId().equals(user.getUserId())) {
                assertUserProfileFieldsMatches(userToRemind1, user);
            } else if (userToRemind2.getUserId().equals(user.getUserId())) {
                assertUserProfileFieldsMatches(userToRemind2, user);
            } else {
                Assert.fail();
            }
        }
    }

    @Test
    public void deleteAll() {
        UserProfile userToPersist1 = generateDefaultTestUserProfile();
        UserProfile userToPersist2 = generateDefaultTestUserProfile();
        userProfileDao.saveNewUser(userToPersist1);
        userProfileDao.saveNewUser(userToPersist2);

        userProfileDao.deleteAll();

        Assert.assertEquals(0, userProfileDao.findAll().size());
    }

    private UserProfile generateDefaultTestUserProfile() {
        UserProfile user = new UserProfile();
        user.setUserId(IdGenerator.generateNewId());
        user.setName("John Doe " + user.getUserId());
        user.setEmail(user.getUserId() + "johntest@test.com");
        user.setLastDonation(TODAY.minusDays(7));
        user.setBloodType(BloodType.A_NEGATIVE);
        user.setDaysBetweenReminders(90);
        user.setNextReminder(TODAY.plusDays(23));
        user.setUserStatus(UserStatus.fromNumberOfElapsedDaysSinceLastDonation(7));
        return user;
    }

    /*
     * Validate if all the fields matches.
     */
    private void assertUserProfileFieldsMatches(UserProfile expected, UserProfile actual) {
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getEmail(), actual.getEmail());
        Assert.assertEquals(expected.getLastDonation(), actual.getLastDonation());
        Assert.assertEquals(expected.getBloodType(), actual.getBloodType());
        Assert.assertEquals(expected.getDaysBetweenReminders(), actual.getDaysBetweenReminders());
        Assert.assertEquals(expected.getNextReminder(), actual.getNextReminder());
        Assert.assertEquals(expected.getUserStatus(), actual.getUserStatus());
    }
}
