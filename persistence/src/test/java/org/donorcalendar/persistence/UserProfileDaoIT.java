package org.donorcalendar.persistence;

import org.donorcalendar.AbstractPersistenceIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserProfileDaoIT extends AbstractPersistenceIntegrationTest {

    private static final LocalDate TODAY = LocalDate.now();

    @Autowired
    private UserProfileDao userProfileDao;

    @Test
    void saveNewUser() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        UserProfile persistedUser = userProfileDao.saveNewUser(userToPersist);

        assertThat(userToPersist).isEqualTo(persistedUser);
    }

    @Test
    void findAll() {
        UserProfile user1 = generateDefaultTestUserProfile();
        UserProfile user2 = generateDefaultTestUserProfile();
        userProfileDao.saveNewUser(user1);
        userProfileDao.saveNewUser(user2);

        List<UserProfile> allPersistedUsers = userProfileDao.findAll();

        assertThat(allPersistedUsers)
                .hasSize(2)
                .containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void findById() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        userProfileDao.saveNewUser(userToPersist);

        UserProfile persistedUser = userProfileDao.findById(userToPersist.getUserId()).orElse(null);

        assertThat(userToPersist).isEqualTo(persistedUser);
    }

    @Test
    void findByEmail() {
        UserProfile userToPersist = generateDefaultTestUserProfile();
        userProfileDao.saveNewUser(userToPersist);

        UserProfile persistedUser = userProfileDao.findByEmail(userToPersist.getEmail()).orElse(null);

        assertThat(userToPersist).isEqualTo(persistedUser);
    }

    @Test
    void exists() {
        UserProfile userToPersist = generateDefaultTestUserProfile();

        boolean beforeSave = userProfileDao.existsById(userToPersist.getUserId());
        userProfileDao.saveNewUser(userToPersist);
        boolean afterSave = userProfileDao.existsById(userToPersist.getUserId());

        assertThat(beforeSave).isFalse();
        assertThat(afterSave).isTrue();
    }

    @Test
    void updateUser() {
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
        assertThat(userForUpdateRequest).isEqualTo(updatedUserProfile);
    }

    @Test
    void findUsersToRemind() {
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

        List<UserProfile> result = userProfileDao.findUsersToRemind();

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(userToRemind1, userToRemind2);
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
}
