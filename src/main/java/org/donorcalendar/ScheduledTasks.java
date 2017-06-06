package org.donorcalendar;

import org.donorcalendar.domain.UserStatus;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public ScheduledTasks(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void sendEmailToRememberDonors() {
        for (UserProfileEntity user : userProfileRepository.findUsersToRemind()) {
            log.info("Sent reminder for user: " + user.getName());
            user.setNextReminder(user.getNextReminder().plusDays(user.getDaysBetweenReminders()));
            userProfileRepository.save(user);
        }
    }

    @Scheduled(fixedRate = 15000)
    public void updateUsersStatus() {
        for (UserProfileEntity user : userProfileRepository.findAll()) {
            UserStatus currentStatus = user.getUserStatus();
            long daysSinceLastDonation = ChronoUnit.DAYS.between(user.getLastDonation(), LocalDate.now());
            UserStatus newStatus = UserStatus.getStatusByNumberOfDaysSinceLastDonation(daysSinceLastDonation);
            if (currentStatus != newStatus) {
                log.info("User status changed: " + user.getName());
                user.setUserStatus(newStatus);
                userProfileRepository.save(user);
            }

        }
    }
}
