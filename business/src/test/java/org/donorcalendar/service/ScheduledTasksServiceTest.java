package org.donorcalendar.service;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.donorcalendar.model.UserProfile.UserProfileBuilder;
import static org.mockito.Mockito.*;

class ScheduledTasksServiceTest {

    private UserProfileDao userProfileDao;

    private ScheduledTasksService target;

    @BeforeEach
    void setUp() {
        userProfileDao = Mockito.mock(UserProfileDao.class);
        target = new ScheduledTasksService(userProfileDao);
    }

    @Test
    void sendEmailToRememberDonors() {
        UserProfileBuilder userBuilder = createTestUserSkeleton();
        userBuilder.nextReminder(LocalDate.now().minusDays(1));
        userBuilder.daysBetweenReminders(4);
        UserProfile user = userBuilder.build();
        when(userProfileDao.findUsersToRemind()).thenReturn(Collections.singletonList(user));
        ArgumentCaptor<UserProfile> userProfileArgumentCaptor = ArgumentCaptor.forClass(UserProfile.class);

        target.sendEmailToRememberDonors();

        verify(userProfileDao, times(1)).updateUser(userProfileArgumentCaptor.capture());
        assertThat(userProfileArgumentCaptor.getValue().getNextReminder()).isEqualTo(LocalDate.now().plusDays(user.getDaysBetweenReminders()));
    }

    @Test
    void updateUsersStatusTask() {
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
        assertThat(userProfileArgumentCaptor.getValue().getUserStatus()).isEqualTo(UserStatus.POTENTIAL_DONOR);
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
