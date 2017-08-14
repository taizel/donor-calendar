package org.donorcalendar;

import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDate;

@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final UserProfileRepository userProfileRepository;

    private final UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Autowired
    public Application(UserProfileRepository userProfileRepository, UserSecurityDetailsRepository userSecurityDetailsRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userSecurityDetailsRepository = userSecurityDetailsRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        insertTestData();
    }

    private void insertTestData() {
        // save a couple of users
        UserProfileEntity user1 = new UserProfileEntity();
        user1.setUserId(System.currentTimeMillis() - 1);
        user1.setName("Frodo");
        user1.setEmail("frodo@middlehearth.com");
        user1.setBloodType(BloodType.AB_NEGATIVE);
        user1.setLastDonation(LocalDate.now().minusDays(7));
        user1.setDaysBetweenReminders(7);
        user1.setNextReminder(LocalDate.now());
        user1.setUserStatus(UserStatus.DONOR);
        user1 = userProfileRepository.save(user1);

        UserSecurityDetailsEntity securityDetailsUser1 = new UserSecurityDetailsEntity();
        securityDetailsUser1.setUserId(user1.getUserId());
        securityDetailsUser1.setPassword("$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a"); //pass1
        userSecurityDetailsRepository.save(securityDetailsUser1);

        UserProfileEntity user2 = new UserProfileEntity();
        user2.setUserId(System.currentTimeMillis());
        user2.setName("Bilbo");
        user2.setEmail("bilbo@middlehearth.com");
        user2.setBloodType(BloodType.A_NEGATIVE);
        user2.setLastDonation(LocalDate.now().minusDays(14));
        user2.setDaysBetweenReminders(14);
        user2.setNextReminder(LocalDate.now());
        user2.setUserStatus(UserStatus.DONOR);
        userProfileRepository.save(user2);

        UserSecurityDetailsEntity securityDetailsUser2 = new UserSecurityDetailsEntity();
        securityDetailsUser2.setUserId(user2.getUserId());
        securityDetailsUser2.setPassword("$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe");//pass2
        userSecurityDetailsRepository.save(securityDetailsUser2);

        // fetch all users
        log.info("Users found with findAll():");
        log.info("-------------------------------");

        for (UserProfileEntity user : userProfileRepository.findAll()) {
            log.info(user.toString());
        }
    }
}