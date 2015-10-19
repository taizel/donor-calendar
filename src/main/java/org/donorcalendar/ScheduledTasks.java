package org.donorcalendar;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    UserRepository userRepository;

    @Scheduled(fixedRate = 10000)
    public void sendEmailToRememerDonors() {

        for (User user : userRepository.findUsersToRemind()) {
            log.info("Sent reminder for user: " + user.getName());
            user.setNextReminder(user.getNextReminder().plusDays(user.getDaysBetweenReminders()));
            userRepository.save(user);
        }
    }
}
