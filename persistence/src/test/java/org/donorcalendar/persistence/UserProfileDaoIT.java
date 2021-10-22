package org.donorcalendar.persistence;

import org.donorcalendar.AbstractPersistenceIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.util.IdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional
public class UserProfileDaoIT extends AbstractPersistenceIntegrationTest {

    private static final LocalDate TODAY = LocalDate.now();

    @Autowired
    private UserProfileDao userProfileDao;

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
            if (userToPersist1.getUserId() == user.getUserId()) {
                assertUserProfileFieldsMatches(userToPersist1, user);
            } else if (userToPersist2.getUserId() == user.getUserId()) {
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

        UserProfile persistedUser = userProfileDao.findById(userToPersist.getUserId()).orElse(null);

        assertUserProfileFieldsMatches(userToPersist, persistedUser);
    }

    @Test
    public void findByEmail() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        userProfileDao.saveNewUser(userToPersist);

        UserProfile persistedUser = userProfileDao.findByEmail(userToPersist.getEmail()).orElse(null);

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
        UserProfile newUser = generateDefaultTestUserProfile();
        newUser = userProfileDao.saveNewUser(newUser);
        UserProfile userForUpdateRequest = new UserProfile.UserProfileBuilder(newUser)
                .userId(newUser.getUserId())
                .name("Updated " + newUser.getName())
                .email("update" + newUser.getEmail())
                .bloodType(BloodType.AB_NEGATIVE)
                .userStatus(UserStatus.DONOR)
                .lastDonation(newUser.getLastDonation().minusDays(1))
                .nextReminder(newUser.getNextReminder().plusDays(90))
                .daysBetweenReminders(newUser.getDaysBetweenReminders() + 30)
                .build();

        userProfileDao.updateUser(userForUpdateRequest);

        UserProfile updatedUserProfile = userProfileDao.findById(newUser.getUserId()).orElse(null);
        assertUserProfileFieldsMatches(userForUpdateRequest, updatedUserProfile);
    }

    @Test
    public void findUsersToRemind() {
        UserProfile userToRemind1 = new UserProfile.UserProfileBuilder(generateDefaultTestUserProfile())
                .nextReminder(TODAY).build();
        UserProfile userToRemind2 = new UserProfile.UserProfileBuilder(generateDefaultTestUserProfile())
                .nextReminder(TODAY.minusDays(1)).build();
        UserProfile userToIgnore1 = new UserProfile.UserProfileBuilder(generateDefaultTestUserProfile())
                .nextReminder(TODAY.plusDays(1)).build();
        UserProfile userToIgnore2 = new UserProfile.UserProfileBuilder(generateDefaultTestUserProfile())
                .nextReminder(null).build();
        userProfileDao.saveNewUser(userToRemind1);
        userProfileDao.saveNewUser(userToRemind2);
        userProfileDao.saveNewUser(userToIgnore1);
        userProfileDao.saveNewUser(userToIgnore2);

        List<UserProfile> allPersistedUsers = userProfileDao.findUsersToRemind();

        Assert.assertEquals(2, allPersistedUsers.size());
        for (UserProfile user : allPersistedUsers) {
            if (userToRemind1.getUserId() == user.getUserId()) {
                assertUserProfileFieldsMatches(userToRemind1, user);
            } else if (userToRemind2.getUserId() == user.getUserId()) {
                assertUserProfileFieldsMatches(userToRemind2, user);
            } else {
                Assert.fail();
            }
        }
    }

    private UserProfile generateDefaultTestUserProfile() {
        long generateId = IdGenerator.generateNewId();
        UserProfile.UserProfileBuilder builder = new UserProfile.UserProfileBuilder(
                generateId,
                "John Doe " + generateId,
                generateId + "johntest@test.com",
                BloodType.A_NEGATIVE,
                UserStatus.fromNumberOfElapsedDaysSinceLastDonation(7)
        );
        builder.lastDonation(TODAY.minusDays(7));
        builder.daysBetweenReminders(90);
        builder.nextReminder(TODAY.plusDays(23));
        return builder.build();
    }

    /*
     * Validate if all the fields matches.
     */
    private void assertUserProfileFieldsMatches(UserProfile expected, UserProfile actual) {
        Assert.assertNotNull(actual);
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
