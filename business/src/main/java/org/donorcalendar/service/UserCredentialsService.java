package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.persistence.UserCredentialsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class UserCredentialsService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserCredentialsDao userCredentialsDao;

    @Autowired
    UserCredentialsService(UserCredentialsDao userCredentialsDao) {
        this.userCredentialsDao = userCredentialsDao;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    void saveNewUserCredentials(User user) {
        UserCredentials userCredentials = new UserCredentials(user.getUserSecurity());
        userCredentials.setPassword(passwordEncoder.encode(userCredentials.getPassword()));
        userCredentialsDao.saveNewUserCredentials(user.getUserProfile().getUserId(), userCredentials);
    }

    void updateUserPassword(Long userId, String newPassword) {
        userCredentialsDao.updateUserPassword(userId, passwordEncoder.encode(newPassword));
    }
}