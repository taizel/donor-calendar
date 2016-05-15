package org.donorcalendar.web;

import org.donorcalendar.domain.User;
import org.donorcalendar.security.UserDetailsImpl;
import org.donorcalendar.service.UserService;
import org.donorcalendar.util.TypeConverter;
import org.donorcalendar.validation.ClientErrorInformation;
import org.donorcalendar.validation.ValidationException;
import org.donorcalendar.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public UserDto saveUser(@RequestBody UserDto userDto) throws ValidationException {
        userService.saveUser(userDtoToUser(userDto));

        System.out.println("saved: " + userDto);
        return userDto;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserDto getLoggedUser(@AuthenticationPrincipal UserDetailsImpl userDetails) throws ValidationException {
        return userToUserDto(userDetails.getUser());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ClientErrorInformation> handleValidationError(HttpServletRequest req, ValidationException e) {
        ClientErrorInformation error = new ClientErrorInformation(e.getMessage(), req.getRequestURI(), req.getMethod());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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

    private UserDto userToUserDto(User user) throws ValidationException {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setBloodType(user.getBloodType());
        userDto.setLastDonation(user.getLastDonation().toString());
        return userDto;
    }
}
