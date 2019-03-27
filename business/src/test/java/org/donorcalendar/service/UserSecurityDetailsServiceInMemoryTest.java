package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.persistence.UserSecurityDetailsDaoInMemoryImpl;
import org.donorcalendar.util.IdGenerator;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertTrue;

public class UserSecurityDetailsServiceInMemoryTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserSecurityDetailsDaoInMemoryImpl userSecurityDao = new UserSecurityDetailsDaoInMemoryImpl();

    private final UserSecurityDetailsService target = new UserSecurityDetailsService(userSecurityDao);

    @Test
    public void saveNewUserSecurityDetails_ValidDetails_Success() {
        User user = createUserForTest();

        target.saveNewUserSecurityDetails(user);

        UserSecurityDetails userSecurityDetails = userSecurityDao.findByUserId(user.getUserProfile().getUserId());
        assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(user.getUserSecurity().getPassword(), userSecurityDetails.getPassword()));
    }

    @Test
    public void updateUserPassword_ValidDetails_Success() {
        User user = createUserForTest();
        Long userId = user.getUserProfile().getUserId();
        String newPassword = "updatedPassword";
        target.saveNewUserSecurityDetails(user);

        target.updateUserPassword(userId, newPassword);

        UserSecurityDetails updatedSecurityDetails = userSecurityDao.findByUserId(userId);
        assertTrue("Encrypted password does not look to be valid.",
                passwordEncoder.matches(newPassword, updatedSecurityDetails.getPassword()));
    }

    private User createUserForTest() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(IdGenerator.generateNewId());
        return new User(userProfile, new UserSecurityDetails("password" + userProfile.getUserId()));
    }
}
