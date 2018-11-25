package org.donorcalendar;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ScheduledTasks {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final UserProfileDao userProfileDao;

    @Autowired
    public ScheduledTasks(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    @Scheduled(fixedRate = 10000)
    public void sendEmailToRememberDonors() {
        for (UserProfile user : userProfileDao.findUsersToRemind()) {
            log.info("Sent reminder for user: " + user.getName());
            user.setNextReminder(user.getNextReminder().plusDays(user.getDaysBetweenReminders()));
            userProfileDao.updateUser(user);
        }
    }

    @Scheduled(fixedRate = 15000)
    public void updateUsersStatus() {
        for (UserProfile user : userProfileDao.findAll()) {
            LocalDate lastDonation = user.getLastDonation();
            if (lastDonation == null) {
                continue;
            }
            UserStatus currentStatus = user.getUserStatus();
            long daysSinceLastDonation = ChronoUnit.DAYS.between(lastDonation, LocalDate.now());
            UserStatus newStatus = UserStatus.fromNumberOfElapsedDaysSinceLastDonation(daysSinceLastDonation);
            if (currentStatus != newStatus) {
                log.info("User status changed: " + user.getName());
                user.setUserStatus(newStatus);
                userProfileDao.updateUser(user);
            }

        }
    }
}
