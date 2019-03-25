package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.persistence.UserSecurityDetailsDao;
import org.donorcalendar.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class UserSecurityDetailsServiceImpl implements UserSecurityDetailsService {

    private final PasswordEncoder passwordEncoder;

    private final UserSecurityDetailsDao userSecurityDetailsDao;

    @Autowired
    UserSecurityDetailsServiceImpl(UserSecurityDetailsDao userSecurityDetailsDao, PasswordEncoder passwordEncoder) {
        this.userSecurityDetailsDao = userSecurityDetailsDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveNewUserSecurityDetails(User user) {
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(user.getUserSecurity());
        userSecurityDetails.setPassword(passwordEncoder.encode(userSecurityDetails.getPassword()));
        userSecurityDetailsDao.saveNewUserSecurityDetails(user.getUserProfile().getUserId(), userSecurityDetails);
    }

    @Override
    public void updateUserPassword(Long userId, String newPassword) {
        userSecurityDetailsDao.updateUserPassword(userId, passwordEncoder.encode(newPassword));
    }
}