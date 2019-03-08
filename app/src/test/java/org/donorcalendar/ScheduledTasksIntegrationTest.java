package org.donorcalendar;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileDao;
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
public class ScheduledTasksIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    UserProfileDao userProfileDao;

    @Autowired
    ScheduledTasks scheduledTasks;

    @Test
    @Transactional
    @Rollback
    public void testUpdateUsersStatusTask() {
        UserProfile user = new UserProfile();
        user.setUserId(IdGenerator.generateNewId());
        user.setName("John Doe");
        user.setEmail("johntest@test.com");
        user.setBloodType(BloodType.A_NEGATIVE);
        user.setLastDonation(LocalDate.now().minusDays(57));
        user.setUserStatus(UserStatus.fromNumberOfElapsedDaysSinceLastDonation(0));
        user = userProfileDao.saveNewUser(user);

        Assert.assertEquals(UserStatus.DONOR, user.getUserStatus());
        scheduledTasks.updateUsersStatus();
        Optional<UserProfile> optional = userProfileDao.findById(user.getUserId());
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(UserStatus.POTENTIAL_DONOR, optional.get().getUserStatus());
    }
}
