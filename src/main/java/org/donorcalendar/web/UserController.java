package org.donorcalendar.web;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserProfile;
import org.donorcalendar.domain.UserSecurityDetails;
import org.donorcalendar.exception.ClientErrorInformation;
import org.donorcalendar.exception.ValidationException;
import org.donorcalendar.security.UserDetailsImpl;
import org.donorcalendar.service.UserService;
import org.donorcalendar.util.TypeConverter;
import org.donorcalendar.web.dto.NewUserDto;
import org.donorcalendar.web.dto.UpdateUserPasswordDto;
import org.donorcalendar.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserDto saveUser(@RequestBody NewUserDto newUserDto) throws ValidationException {
        User user = newUserDtoToUser(newUserDto);
        UserDto userDto = userToUserDto(userService.saveNewUser(user));
        return userDto;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public UserDto updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserDto userDto) throws ValidationException {

        Long userId = userDetails.getUserProfile().getUserId();
        UserProfile userProfileToUpdate = userDtoToUser(userDto);
        userProfileToUpdate.setUserId(userId);

        userService.updateExistingUser(userProfileToUpdate);
        return userToUserDto(userProfileToUpdate);
    }

    @RequestMapping(value = "/update-password", method = RequestMethod.PUT)
    public ResponseEntity updateUserPassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UpdateUserPasswordDto updateUserPasswordDtoDto) throws ValidationException {

        UserProfile userProfile = userDetails.getUserProfile();
        userService.updateUserPassword(userProfile.getUserId(), updateUserPasswordDtoDto.getNewPassword());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET)
    public UserDto getLoggedUser(@AuthenticationPrincipal UserDetailsImpl userDetails) throws ValidationException {
        return userToUserDto(userDetails.getUserProfile());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ClientErrorInformation> handleValidationError(HttpServletRequest req, ValidationException e) {
        ClientErrorInformation error = new ClientErrorInformation(e.getMessage(), req.getRequestURI(), req.getMethod());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private User newUserDtoToUser(NewUserDto newUserDto) throws ValidationException {
        UserProfile userProfile = userDtoToUser(newUserDto);
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(newUserDto.getPassword());
        return new User(userProfile, userSecurityDetails);
    }

    private UserProfile userDtoToUser(UserDto userDto) throws ValidationException {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(userDto.getEmail());
        userProfile.setName(userDto.getName());
        userProfile.setBloodType(userDto.getBloodType());
        if (userDto.getLastDonation() != null) {
            try {
                LocalDate lastDonation = TypeConverter.stringToLocalDate(userDto.getLastDonation());
                userProfile.setLastDonation(lastDonation);
            } catch (DateTimeParseException e) {
                throw new ValidationException("Invalid date format for lastDonation field.");
            }
        }
        return userProfile;
    }

    private UserDto userToUserDto(UserProfile userProfile) throws ValidationException {
        UserDto updateUserDto = new UserDto();
        updateUserDto.setEmail(userProfile.getEmail());
        updateUserDto.setName(userProfile.getName());
        updateUserDto.setBloodType(userProfile.getBloodType());
        if (userProfile.getLastDonation() != null) {
            updateUserDto.setLastDonation(userProfile.getLastDonation().toString());
        }
        return updateUserDto;
    }
}
