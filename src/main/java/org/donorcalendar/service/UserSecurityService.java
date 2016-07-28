package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserSecurityDetails;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService {

    private final BCryptPasswordEncoder passwordEncoder;


    private UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Autowired
    public UserSecurityService(UserSecurityDetailsRepository userSecurityDetailsRepository) {
        this.userSecurityDetailsRepository = userSecurityDetailsRepository;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserSecurityDetails saveNewUserSecurityDetails(User user) {
        UserSecurityDetailsEntity userSecurityDetailsEntity = new UserSecurityDetailsEntity();
        userSecurityDetailsEntity.setUserId(user.getUserProfile().getUserId());
        userSecurityDetailsEntity.setPassword(passwordEncoder.encode(user.getUserSecurity().getPassword()));
        userSecurityDetailsEntity = userSecurityDetailsRepository.save(userSecurityDetailsEntity);
        return new UserSecurityDetails(userSecurityDetailsEntity.getPassword());
    }
}