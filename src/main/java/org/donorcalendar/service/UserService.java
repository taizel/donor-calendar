package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserRepository;
import org.donorcalendar.util.TypeConverter;
import org.donorcalendar.validation.ValidationException;
import org.donorcalendar.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public void saveUser(UserDto userDto) throws ValidationException {
        userRepository.save(userDtoToUser(userDto));
    }

    private User userDtoToUser(UserDto userDto) throws ValidationException {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setBloodType(userDto.getBloodType());
        try {
            LocalDate lastDonation = TypeConverter.stringToLocalDate(userDto.getLastDonation());
            user.setLastDonation(lastDonation);
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format for lastDonation field.");
        }
        return user;
    }
}
