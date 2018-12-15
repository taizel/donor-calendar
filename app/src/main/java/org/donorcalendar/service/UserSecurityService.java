package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.persistence.UserSecurityDetailsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class UserSecurityService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserSecurityDetailsDao userSecurityDetailsDao;

    @Autowired
    UserSecurityService(UserSecurityDetailsDao userSecurityDetailsDao) {
        this.userSecurityDetailsDao = userSecurityDetailsDao;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    void saveNewUserSecurityDetails(User user) {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(user.getUserSecurity());
        userSecurityDetails.setPassword(passwordEncoder.encode(userSecurityDetails.getPassword()));
        userSecurityDetailsDao.saveNewUserSecurityDetails(user.getUserProfile().getUserId(), userSecurityDetails);
    }

    void updateUserPassword(Long userId, String newPassword) {
        userSecurityDetailsDao.updateUserPassword(userId, passwordEncoder.encode(newPassword));
    }
}