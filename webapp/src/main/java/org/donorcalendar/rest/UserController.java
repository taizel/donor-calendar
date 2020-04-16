package org.donorcalendar.rest;

import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.User;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.ValidationException;
import org.donorcalendar.rest.dto.NewUserDto;
import org.donorcalendar.rest.dto.UpdateUserDto;
import org.donorcalendar.rest.dto.UpdateUserPasswordDto;
import org.donorcalendar.rest.dto.UserResponseDto;
import org.donorcalendar.security.UserSecurityDetails;
import org.donorcalendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDto createNewUser(@RequestBody NewUserDto newUserDto) throws ValidationException {
        User user = newUserDtoToUser(newUserDto);
        return UserResponseDto.buildUserDtoFromUserProfile(userService.saveNewUser(user));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDto updateUser(@AuthenticationPrincipal UserSecurityDetails userDetails, @RequestBody UpdateUserDto userDto) throws ValidationException, NotFoundException {
        Long userId = userDetails.getUserProfile().getUserId();
        UserProfile userProfileToUpdate = userDto.buildUserProfile();
        userProfileToUpdate.setUserId(userId);

        userService.updateUserProfile(userProfileToUpdate);
        return UserResponseDto.buildUserDtoFromUserProfile(userProfileToUpdate);
    }

    @PutMapping(path = "/update-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> updateUserPassword(@AuthenticationPrincipal UserSecurityDetails userDetails, @RequestBody UpdateUserPasswordDto updateUserPasswordDtoDto) throws ValidationException, NotFoundException {
        UserProfile userProfile = userDetails.getUserProfile();
        userService.updateUserPassword(userProfile.getUserId(), updateUserPasswordDtoDto.getNewPassword());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public UserResponseDto getLoggedUser(@AuthenticationPrincipal UserSecurityDetails userDetails) {
        return UserResponseDto.buildUserDtoFromUserProfile(userDetails.getUserProfile());
    }

    private User newUserDtoToUser(NewUserDto newUserDto) {
        UserProfile userProfile = newUserDto.buildUserProfile();
        UserCredentials userCredentials = new UserCredentials(newUserDto.getPassword());
        return new User(userProfile, userCredentials);
    }
}
