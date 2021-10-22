package org.donorcalendar.service;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.util.IdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.donorcalendar.model.UserProfile.UserProfileBuilder;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
        UserProfileBuilder userBuilder = createTestUserSkeleton();
        userBuilder.nextReminder(LocalDate.now().minusDays(1));
        userBuilder.daysBetweenReminders(4);
        UserProfile user = userBuilder.build();
        when(userProfileDao.findUsersToRemind()).thenReturn(Collections.singletonList(user));
        ArgumentCaptor<UserProfile> userProfileArgumentCaptor = ArgumentCaptor.forClass(UserProfile.class);

        target.sendEmailToRememberDonors();

        verify(userProfileDao, times(1)).updateUser(userProfileArgumentCaptor.capture());
        assertEquals(LocalDate.now().plusDays(user.getDaysBetweenReminders()), userProfileArgumentCaptor.getValue().getNextReminder());
    }

    @Test
    public void testUpdateUsersStatusTask() {
        UserProfileBuilder userToUpdateBuilder = createTestUserSkeleton();
        userToUpdateBuilder.lastDonation(LocalDate.now().minusDays(57));
        userToUpdateBuilder.userStatus(UserStatus.DONOR);
        UserProfile userToUpdate = userToUpdateBuilder.build();

        UserProfileBuilder userToDoNotUpdateBuilder = createTestUserSkeleton();
        userToDoNotUpdateBuilder.lastDonation(LocalDate.now());
        userToDoNotUpdateBuilder.userStatus(UserStatus.DONOR);
        UserProfile userToDoNotUpdate = userToDoNotUpdateBuilder.build();

        UserProfile userThatNeverDonated = createTestUserSkeleton().build();

        when(userProfileDao.findAll()).thenReturn(Arrays.asList(userToUpdate, userToDoNotUpdate, userThatNeverDonated));
        ArgumentCaptor<UserProfile> userProfileArgumentCaptor = ArgumentCaptor.forClass(UserProfile.class);

        target.updateUsersStatus();

        verify(userProfileDao, times(1)).updateUser(userProfileArgumentCaptor.capture());
        assertEquals(UserStatus.POTENTIAL_DONOR, userProfileArgumentCaptor.getValue().getUserStatus());
    }

    private UserProfileBuilder createTestUserSkeleton() {
        long newUserId = IdGenerator.generateNewId();
        return new UserProfileBuilder(
                newUserId,
                "John Doe " + newUserId,
                newUserId + "johntest@test.com",
                BloodType.A_NEGATIVE,
                null
        );
    }
}
