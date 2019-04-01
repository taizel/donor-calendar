package org.donorcalendar.rest;

import org.apache.http.HttpStatus;
import org.donorcalendar.AbstractRestAssuredIntegrationTest;
import org.donorcalendar.model.BloodType;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.persistence.UserCredentialsEntity;
import org.donorcalendar.persistence.UserProfileEntity;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserCredentialsRepository;
import org.donorcalendar.util.IdGenerator;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public class DonorZoneControllerIT extends AbstractRestAssuredIntegrationTest {

    private static final String JOHN_UNENCRYPTED_PASSWORD = "pass1";
    private static final String JOHN_ENCRYPTED_PASSWORD = "$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a";
    private static final String BILBO_UNENCRYPTED_PASSWORD = "pass2";
    private static final String BILBO_ENCRYPTED_PASSWORD = "$2a$10$ygbIolKsXFB6JnbVjnrhI.OWgW4nqgfIBLszx3eFxaJ1H7w/5tILe";

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    private UserProfileEntity john;
    private UserProfileEntity bilbo;

    @Override
    public void setUp() {
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

        john = userProfileRepository.save(john);
        bilbo = userProfileRepository.save(bilbo);

        UserCredentialsEntity userCredentialsEntityJohn = new UserCredentialsEntity();
        userCredentialsEntityJohn.setUserId(john.getUserId());
        userCredentialsEntityJohn.setPassword(JOHN_ENCRYPTED_PASSWORD);

        UserCredentialsEntity userCredentialsEntityBilbo = new UserCredentialsEntity();
        userCredentialsEntityBilbo.setUserId(bilbo.getUserId());
        userCredentialsEntityBilbo.setPassword(BILBO_ENCRYPTED_PASSWORD);

        userCredentialsRepository.save(userCredentialsEntityJohn);
        userCredentialsRepository.save(userCredentialsEntityBilbo);
    }

    @Override
    public void tearDown() {
        userCredentialsRepository.deleteAll();
        userProfileRepository.deleteAll();
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
            body(".", hasItems("Cool Restaurant", "Nice Burger Place", "Amazing Bakery"));
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
