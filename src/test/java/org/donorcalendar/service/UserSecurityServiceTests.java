package org.donorcalendar.service;

import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.junit.Before;
import org.mockito.Mockito;

public class UserSecurityServiceTests {

    private final String UNENCRYPTED_TEST_PASSWORD = "pass1";
    private final String ENCRYPTED_TEST_PASSWORD = "$2a$10$f2H/Y/6Px.LnaSdKF1.I3uKUqjZ.Da2adgUTM8jT5.sjBJqD4qz1a";

    private UserSecurityDetailsRepository userSecurityDetailsRepository;

    private UserSecurityService target;
    private UserSecurityService mockTarget;

    @Before
    public void setUp(){
        userSecurityDetailsRepository = Mockito.mock(UserSecurityDetailsRepository.class);
        target = new UserSecurityService(userSecurityDetailsRepository);
    }


}
