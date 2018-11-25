package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.persistence.UserSecurityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class UserSecurityService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserSecurityDao userSecurityDao;

    @Autowired
    UserSecurityService(UserSecurityDao userSecurityDao) {
        this.userSecurityDao = userSecurityDao;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    void saveNewUserSecurityDetails(User user) {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(user.getUserSecurity());
        userSecurityDetails.setPassword(passwordEncoder.encode(userSecurityDetails.getPassword()));
        userSecurityDao.saveNewUserSecurityDetails(user.getUserProfile().getUserId(), userSecurityDetails);
    }

    void updateUserPassword(Long userId, String newPassword) {
        userSecurityDao.updateUserPassword(userId, passwordEncoder.encode(newPassword));
    }
}