package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.persistence.UserRepository;
import org.donorcalendar.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void saveUser(User user) throws ValidationException {
        userRepository.save(user);
    }
}
