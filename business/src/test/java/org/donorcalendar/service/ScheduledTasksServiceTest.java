package org.donorcalendar.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.util.IdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

public class ScheduledTasksServiceTest {

    private UserProfileDao userProfileDao;

    private ScheduledTasksService target;

    @Before
    public void setUp() {
        userProfileDao = Mockito.mock(UserProfileDao.class);
        target = new ScheduledTasksService(userProfileDao);
    }

    @Test
    public void testSendEmailToRememberDonors() {
        UserProfile user = createTestUserSkeleton();
        user.setNextReminder(LocalDate.now().minusDays(1));
        user.setDaysBetweenReminders(4);
        when(userProfileDao.findUsersToRemind()).thenReturn(Collections.singletonList(user));

        target.sendEmailToRememberDonors();

        assertEquals(LocalDate.now().plusDays(user.getDaysBetweenReminders()), user.getNextReminder());
    }

    @Test
    public void testUpdateUsersStatusTask() {
        UserProfile userToUpdate = createTestUserSkeleton();
        userToUpdate.setLastDonation(LocalDate.now().minusDays(57));
        userToUpdate.setUserStatus(UserStatus.DONOR);
        UserProfile userToDoNotUpdate = createTestUserSkeleton();
        userToDoNotUpdate.setLastDonation(LocalDate.now());
        userToDoNotUpdate.setUserStatus(UserStatus.DONOR);
        UserProfile userThatNeverDonated = createTestUserSkeleton(); // if never donated status can't be updated
        userThatNeverDonated.setUserStatus(UserStatus.NEED_TO_DONATE);
        when(userProfileDao.findAll()).thenReturn(Arrays.asList(userToUpdate, userToDoNotUpdate, userThatNeverDonated));

        target.updateUsersStatus();

        assertEquals(UserStatus.POTENTIAL_DONOR, userToUpdate.getUserStatus());
        assertEquals(UserStatus.DONOR, userToDoNotUpdate.getUserStatus());
        assertEquals(UserStatus.NEED_TO_DONATE, userThatNeverDonated.getUserStatus());
    }

    private UserProfile createTestUserSkeleton() {
        UserProfile user = new UserProfile();
        user.setUserId(IdGenerator.generateNewId());
        user.setName("John Doe " + user.getUserId());
        user.setEmail(user.getUserId() + "johntest@test.com");
        user.setBloodType(BloodType.A_NEGATIVE);
        return user;
    }
}
