package org.donorcalendar;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserRepository;
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

    @Autowired
    UserRepository userRepository;

    @Scheduled(fixedRate = 10000)
    public void sendEmailToRememerDonors() {

        for (User user : userRepository.findAll()) {
            if (user.getLastDonation() != null && user.getIntervalOfDaysBetweenReminders() > 0) {
                Long daysBetween = ChronoUnit.DAYS.between(user.getLastDonation(), LocalDate.now());
                if (daysBetween > 0 && daysBetween % user.getIntervalOfDaysBetweenReminders() == 0) {
                    log.info("Sent reminder for user: " + user.getName());
                }
            }
        }
    }
}
