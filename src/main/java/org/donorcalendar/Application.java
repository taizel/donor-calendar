package org.donorcalendar;

import org.donorcalendar.domain.BloodType;
import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserRepository;
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
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        // save a couple of customers
        User frodo = new User();

        frodo.setName("Frodo");
        frodo.setEmail("frodo@middlehearth.com");
        frodo.setBloodType(BloodType.AB_NEGATIVE);
        frodo.setLastDonation(LocalDate.now().minusDays(7));
        frodo.setDaysBetweenReminders(7);
        frodo.setNextReminder(LocalDate.now());
        userRepository.save(frodo);

        User bilbo = new User();
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
        bilbo.setLastDonation(LocalDate.now().minusDays(14));
        bilbo.setDaysBetweenReminders(14);
        frodo.setNextReminder(LocalDate.now());
        userRepository.save(bilbo);

        // fetch all users
        log.info("Users found with findAll():");
        log.info("-------------------------------");

        for (User user : userRepository.findAll()) {
            log.info(user.toString());
        }
    }
}