package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserRepository;
import org.donorcalendar.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void saveUser(UserDto userDto){
        userRepository.save(userDtoToUser(userDto));
    }

    private User userDtoToUser(UserDto userDto){
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setLastDonation(LocalDate.now());
        return user;
    }
}
