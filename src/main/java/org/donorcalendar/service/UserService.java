package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.persistence.UserRepository;
import org.donorcalendar.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public UserService() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public void saveNewUser(User user) throws ValidationException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateExistingUser(User user) throws ValidationException {
        userRepository.save(user);
    }
}
