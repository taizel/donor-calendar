package org.donorcalendar;

import org.donorcalendar.domain.BloodType;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        // save a couple of users
        UserProfileEntity frodo = new UserProfileEntity();

        frodo.setName("Frodo");
        frodo.setEmail("frodo@middlehearth.com");
        frodo.setBloodType(BloodType.AB_NEGATIVE);
        frodo.setLastDonation(LocalDate.now().minusDays(7));
        frodo.setDaysBetweenReminders(7);
        frodo.setNextReminder(LocalDate.now());
//        frodo.setPassword("$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a"); //pass1
        userRepository.save(frodo);

        UserProfileEntity bilbo = new UserProfileEntity();
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
        bilbo.setLastDonation(LocalDate.now().minusDays(14));
        bilbo.setDaysBetweenReminders(14);
        bilbo.setNextReminder(LocalDate.now());
//        bilbo.setPassword("$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe");//pass2
        userRepository.save(bilbo);

        // fetch all users
        log.info("Users found with findAll():");
        log.info("-------------------------------");

        for (UserProfileEntity user : userRepository.findAll()) {
            log.info(user.toString());
        }
    }
}