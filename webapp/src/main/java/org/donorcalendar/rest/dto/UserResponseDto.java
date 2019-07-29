package org.donorcalendar.rest.dto;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;

public class UserResponseDto extends UserDto {
    private UserStatus userStatus;

    public static UserResponseDto buildUserDtoFromUserProfile(UserProfile userProfile) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail(userProfile.getEmail());
        userResponseDto.setName(userProfile.getName());
        userResponseDto.setBloodType(userProfile.getBloodType());
        userResponseDto.setLastDonation(userProfile.getLastDonation());
        userResponseDto.setUserStatus(userProfile.getUserStatus());
        userResponseDto.setDaysBetweenReminders(userProfile.getDaysBetweenReminders());
        userResponseDto.setNextReminder(userProfile.getNextReminder());
        return userResponseDto;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }
}
