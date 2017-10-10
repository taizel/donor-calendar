package org.donorcalendar.web;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.donorcalendar.util.IdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DonorZoneControllerIT {

    private static final String JOHN_UNENCRYPTED_PASSWORD = "pass1";
    private static final String JOHN_ENCRYPTED_PASSWORD = "$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a";
    private static final String BILBO_UNENCRYPTED_PASSWORD = "pass2";
    private static final String BILBO_ENCRYPTED_PASSWORD = "$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe";

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserSecurityDetailsRepository userSecurityDetailsRepository;

    @LocalServerPort
    private int port;

    private UserProfileEntity john;
    private UserProfileEntity bilbo;

    @Before
    public void setUp() {
        john = new UserProfileEntity();
        john.setUserId(IdGenerator.generateNewId());
        john.setName("John");
        john.setEmail("john@middlehearth.com");
        john.setBloodType(BloodType.AB_NEGATIVE);
        john.setLastDonation(LocalDate.now().minusDays(7));
        john.setDaysBetweenReminders(7);
        john.setNextReminder(LocalDate.now());
        john.setUserStatus(UserStatus.DONOR);


        bilbo = new UserProfileEntity();
        bilbo.setUserId(IdGenerator.generateNewId());
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
        bilbo.setLastDonation(LocalDate.now().minusDays(120));
        bilbo.setDaysBetweenReminders(14);
        bilbo.setNextReminder(LocalDate.now());
        bilbo.setUserStatus(UserStatus.NEED_TO_DONATE);

        userProfileRepository.deleteAll();
        john = userProfileRepository.save(john);
        bilbo = userProfileRepository.save(bilbo);

        UserSecurityDetailsEntity userSecurityDetailsEntityJohn = new UserSecurityDetailsEntity();
        userSecurityDetailsEntityJohn.setUserId(john.getUserId());
        userSecurityDetailsEntityJohn.setPassword(JOHN_ENCRYPTED_PASSWORD);

        UserSecurityDetailsEntity userSecurityDetailsEntityBilbo = new UserSecurityDetailsEntity();
        userSecurityDetailsEntityBilbo.setUserId(bilbo.getUserId());
        userSecurityDetailsEntityBilbo.setPassword(BILBO_ENCRYPTED_PASSWORD);

        userSecurityDetailsRepository.deleteAll();
        userSecurityDetailsRepository.save(userSecurityDetailsEntityJohn);
        userSecurityDetailsRepository.save(userSecurityDetailsEntityBilbo);

        RestAssured.port = port;
    }

    @Test
    public void getListOfShoppingFacilities() {
        given().
            auth().basic(john.getEmail(), JOHN_UNENCRYPTED_PASSWORD).
        expect().
            statusCode(HttpStatus.SC_OK).
        when().
            get("/donor-zone").
        then().
            assertThat().
            body("shoppingFacilities", hasItems("Cool Restaurant", "Nice Burger Place", "Amazing Bakery"));
    }

    @Test
    public void validateUserStatus() {
        given().
                auth().basic(bilbo.getEmail(), BILBO_UNENCRYPTED_PASSWORD).
        expect().
                statusCode(HttpStatus.SC_FORBIDDEN).
        when().
                get("/donor-zone").
        then().
                assertThat().
                body("errorMessage", equalTo("You need to be in the status \"Donor\"or \"Potential Donor\" to access this resource."));
    }

}
