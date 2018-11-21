package org.donorcalendar.persistence;

import org.donorcalendar.AbstractIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.util.IdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
public class UserProfileDaoIT extends AbstractIntegrationTest {

    @Autowired
    private UserProfileDao userProfileDao;

    @Test
    @Transactional
    @Rollback
    public void saveNewUserWithIdGeneratedAutomatically() {
        UserProfile userToPersist = generateDefaultTestUserProfile();

        UserProfile persistedUser = userProfileDao.saveNewUser(userToPersist);

        Assert.assertNotNull(persistedUser.getUserId());
        Assert.assertNotEquals(persistedUser.getUserId().longValue(), 0L);
        assertUserProfilesFields(userToPersist, persistedUser);
    }

    @Test
    @Transactional
    @Rollback
    public void saveNewUserWithProvidedId() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        userToPersist.setUserId(IdGenerator.generateNewId());

        UserProfile persistedUser = userProfileDao.saveNewUser(userToPersist);

        Assert.assertEquals(userToPersist.getUserId(), persistedUser.getUserId());
        assertUserProfilesFields(userToPersist, persistedUser);
    }

    @Test
    @Transactional
    @Rollback
    public void findById() {
        UserProfile userToPersist = generateDefaultTestUserProfile();

        Long persistedId = userProfileDao.saveNewUser(userToPersist).getUserId();

        Optional<UserProfile> persistedUser = userProfileDao.findById(persistedId);

        Assert.assertTrue(persistedUser.isPresent());
        UserProfile userProfile = persistedUser.get();
        Assert.assertEquals(persistedId, userProfile.getUserId());
        assertUserProfilesFields(userToPersist, userProfile);
    }

    @Test
    @Transactional
    @Rollback
    public void findByEmail() {
        UserProfile userToPersist = generateDefaultTestUserProfile();

        Long persistedId = userProfileDao.saveNewUser(userToPersist).getUserId();

        UserProfile persistedUser = userProfileDao.findByEmail(userToPersist.getEmail());

        Assert.assertEquals(persistedId, persistedUser.getUserId());
        assertUserProfilesFields(userToPersist, persistedUser);
    }

    @Test
    @Transactional
    @Rollback
    public void exists() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        userToPersist.setUserId(IdGenerator.generateNewId());

        boolean beforeSave = userProfileDao.existsById(userToPersist.getUserId());

        userProfileDao.saveNewUser(userToPersist);

        boolean afterSave = userProfileDao.existsById(userToPersist.getUserId());

        Assert.assertFalse(beforeSave);
        Assert.assertTrue(afterSave);
    }


    private UserProfile generateDefaultTestUserProfile() {
        UserProfile user = new UserProfile();
        user.setName("John Doe");
        user.setEmail("johntest@test.com");
        user.setLastDonation(LocalDate.now().minusDays(7));
        user.setBloodType(BloodType.A_NEGATIVE);
        user.setDaysBetweenReminders(30);
        user.setNextReminder(LocalDate.now().plusDays(23));
        user.setUserStatus(UserStatus.fromNumberOfElapsedDaysSinceLastDonation(7));
        return user;
    }

    /**
     * Validate if all the fields matches, with exception of the ID, which will be null in general for the expected
     * user profile.
     */
    private void assertUserProfilesFields(UserProfile expected, UserProfile actual) {
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getEmail(), actual.getEmail());
        Assert.assertEquals(expected.getLastDonation(), actual.getLastDonation());
        Assert.assertEquals(expected.getBloodType(), actual.getBloodType());
        Assert.assertEquals(expected.getDaysBetweenReminders(), actual.getDaysBetweenReminders());
        Assert.assertEquals(expected.getNextReminder(), actual.getNextReminder());
        Assert.assertEquals(expected.getUserStatus(), actual.getUserStatus());
    }
}
