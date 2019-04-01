package org.donorcalendar.rest;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.ValidationException;
import org.donorcalendar.security.UserSecurityDetails;
import org.donorcalendar.service.UserService;
import org.donorcalendar.rest.dto.NewUserDto;
import org.donorcalendar.rest.dto.UpdateUserPasswordDto;
import org.donorcalendar.rest.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createNewUser(@RequestBody NewUserDto newUserDto) throws ValidationException {
        User user = newUserDtoToUser(newUserDto);
        return userToUserDto(userService.saveNewUser(user));
    }

    @PutMapping
    public UserDto updateUser(@AuthenticationPrincipal UserSecurityDetails userDetails, @RequestBody UserDto userDto) throws ValidationException, NotFoundException {

        Long userId = userDetails.getUserProfile().getUserId();
        UserProfile userProfileToUpdate = userDtoToUser(userDto);
        userProfileToUpdate.setUserId(userId);

        userService.updateUserProfile(userProfileToUpdate);
        return userToUserDto(userProfileToUpdate);
    }

    @PutMapping(path = "/update-password")
    public ResponseEntity updateUserPassword(@AuthenticationPrincipal UserSecurityDetails userDetails, @RequestBody UpdateUserPasswordDto updateUserPasswordDtoDto) throws ValidationException, NotFoundException {

        UserProfile userProfile = userDetails.getUserProfile();
        userService.updateUserPassword(userProfile.getUserId(), updateUserPasswordDtoDto.getNewPassword());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public UserDto getLoggedUser(@AuthenticationPrincipal UserSecurityDetails userDetails) {
        return userToUserDto(userDetails.getUserProfile());
    }

    private User newUserDtoToUser(NewUserDto newUserDto) {
        UserProfile userProfile = userDtoToUser(newUserDto);
        UserCredentials userCredentials = new UserCredentials(newUserDto.getPassword());
        return new User(userProfile, userCredentials);
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
