package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class UserSecurityService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Autowired
    public UserSecurityService(UserSecurityDetailsRepository userSecurityDetailsRepository) {
        this.userSecurityDetailsRepository = userSecurityDetailsRepository;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public void saveNewUserSecurityDetails(User user) {
        UserSecurityDetailsEntity userSecurityDetailsEntity = new UserSecurityDetailsEntity();
        userSecurityDetailsEntity.setUserId(user.getUserProfile().getUserId());
        userSecurityDetailsEntity.setPassword(passwordEncoder.encode(user.getUserSecurity().getPassword()));
        userSecurityDetailsRepository.save(userSecurityDetailsEntity);
    }

    public void updateUserPassword(Long userId, String newPassword) {
        UserSecurityDetailsEntity userSecurityDetailsEntity = userSecurityDetailsRepository.findByUserId(userId);
        userSecurityDetailsEntity.setPassword(passwordEncoder.encode(newPassword));
        userSecurityDetailsRepository.save(userSecurityDetailsEntity);
    }
}