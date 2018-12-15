package org.donorcalendar.web;

import org.apache.http.HttpStatus;
import org.donorcalendar.RestAssuredAbstractIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.donorcalendar.util.IdGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public class DonorZoneControllerIT extends RestAssuredAbstractIntegrationTest {

    private static final String JOHN_UNENCRYPTED_PASSWORD = "pass1";
    private static final String JOHN_ENCRYPTED_PASSWORD = "$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a";
    private static final String BILBO_UNENCRYPTED_PASSWORD = "pass2";
    private static final String BILBO_ENCRYPTED_PASSWORD = "$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe";

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserSecurityDetailsRepository userSecurityDetailsRepository;

    private UserProfileEntity john;
    private UserProfileEntity bilbo;

    @Override
    public void businessSetUp() {
        john = new UserProfileEntity();
        john.setUserId(IdGenerator.generateNewId());
        john.setName("John");
        john.setEmail("john@middlehearth.com");
        john.setBloodType(BloodType.AB_NEGATIVE);
        john.setUserStatus(UserStatus.DONOR);

        bilbo = new UserProfileEntity();
        bilbo.setUserId(IdGenerator.generateNewId());
        bilbo.setName("Bilbo");
        bilbo.setEmail("bilbo@middlehearth.com");
        bilbo.setBloodType(BloodType.A_NEGATIVE);
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
            body("shopping-facilities", hasItems("Cool Restaurant", "Nice Burger Place", "Amazing Bakery"));
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
                body("error-message", equalTo("You need to be in the status \"Donor\"or \"Potential Donor\" to access this resource."));
    }

}
