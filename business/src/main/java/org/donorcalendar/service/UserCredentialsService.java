package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.persistence.UserCredentialsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCredentialsService {

    private final PasswordEncoder passwordEncoder;

    private final UserCredentialsDao userCredentialsDao;

    @Autowired
    public UserCredentialsService(UserCredentialsDao userCredentialsDao) {
        this.userCredentialsDao = userCredentialsDao;
        passwordEncoder = getNewPasswordEncoder();
    }

    void saveNewUserCredentials(User user) {
        UserCredentials userCredentials = new UserCredentials(user.getUserCredentials());
        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        userCredentialsDao.saveNewUserCredentials(user.getUserProfile().getUserId(), userCredentials);
    }

    void updateUserPassword(Long userId, String newPassword) {
        userCredentialsDao.saveUserPassword(userId, passwordEncoder.encode(newPassword));
    }

    public static PasswordEncoder getNewPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}