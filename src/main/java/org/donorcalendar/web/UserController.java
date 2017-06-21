package org.donorcalendar.web;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.exception.NotFoundException;
import org.donorcalendar.exception.ValidationException;
import org.donorcalendar.security.UserAuthenticationDetails;
import org.donorcalendar.service.UserService;
import org.donorcalendar.web.dto.NewUserDto;
import org.donorcalendar.web.dto.UpdateUserPasswordDto;
import org.donorcalendar.web.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserDto createNewUser(@RequestBody NewUserDto newUserDto) throws ValidationException {
        User user = newUserDtoToUser(newUserDto);
        UserDto userDto = userToUserDto(userService.saveNewUser(user));
        return userDto;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public UserDto updateUser(@AuthenticationPrincipal UserAuthenticationDetails userDetails, @RequestBody UserDto userDto) throws ValidationException, NotFoundException {

        Long userId = userDetails.getUserProfile().getUserId();
        UserProfile userProfileToUpdate = userDtoToUser(userDto);
        userProfileToUpdate.setUserId(userId);

        userService.updateExistingUser(userProfileToUpdate);
        return userToUserDto(userProfileToUpdate);
    }

    @RequestMapping(value = "/update-password", method = RequestMethod.PUT)
    public ResponseEntity updateUserPassword(@AuthenticationPrincipal UserAuthenticationDetails userDetails, @RequestBody UpdateUserPasswordDto updateUserPasswordDtoDto) throws ValidationException {

        UserProfile userProfile = userDetails.getUserProfile();
        userService.updateUserPassword(userProfile.getUserId(), updateUserPasswordDtoDto.getNewPassword());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(method = RequestMethod.GET)
    public UserDto getLoggedUser(@AuthenticationPrincipal UserAuthenticationDetails userDetails) throws ValidationException {
        return userToUserDto(userDetails.getUserProfile());
    }

    private User newUserDtoToUser(NewUserDto newUserDto) {
        UserProfile userProfile = userDtoToUser(newUserDto);
        UserSecurityDetails userSecurityDetails = new UserSecurityDetails(newUserDto.getPassword());
        return new User(userProfile, userSecurityDetails);
    }

    private UserProfile userDtoToUser(UserDto userDto) {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail(userDto.getEmail());
        userProfile.setName(userDto.getName());
        userProfile.setBloodType(userDto.getBloodType());
        userProfile.setLastDonation(userDto.getLastDonation());
        return userProfile;
    }

    private UserDto userToUserDto(UserProfile userProfile) {
        UserDto userDto = new UserDto();
        userDto.setEmail(userProfile.getEmail());
        userDto.setName(userProfile.getName());
        userDto.setBloodType(userProfile.getBloodType());
        userDto.setLastDonation(userProfile.getLastDonation());
        userDto.setUserStatus(userProfile.getUserStatus());
        userDto.setDaysBetweenReminders(userProfile.getDaysBetweenReminders());
        userDto.setNextReminder(userProfile.getNextReminder());
        return userDto;
    }
}
