package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.persistence.UserCredentialsDaoInMemoryImpl;
import org.donorcalendar.util.IdGenerator;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertTrue;

public class UserCredentialsServiceInMemoryTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserCredentialsDaoInMemoryImpl userSecurityDao = new UserCredentialsDaoInMemoryImpl();

    private final UserCredentialsService target = new UserCredentialsService(userSecurityDao);

    @Test
    public void saveNewUserCredentials_ValidCredentials_Success() {
        User user = createUserForTest();

        target.saveNewUserCredentials(user);

        UserCredentials userCredentials = userSecurityDao.findByUserId(user.getUserProfile().getUserId()).orElse(null);
        assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(user.getUserCredentials().getPassword(), userCredentials.getPassword()));
    }

    @Test
    public void updateUserPassword_ValidCredentials_Success() {
        User user = createUserForTest();
        Long userId = user.getUserProfile().getUserId();
        String newPassword = "updatedPassword";
        target.saveNewUserCredentials(user);

        target.updateUserPassword(userId, newPassword);

        UserCredentials updatedSecurityDetails = userSecurityDao.findByUserId(userId).orElse(null);
        assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(newPassword, updatedSecurityDetails.getPassword()));
    }

    private User createUserForTest() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(IdGenerator.generateNewId());
        return new User(userProfile, new UserCredentials("password" + userProfile.getUserId()));
    }
}
