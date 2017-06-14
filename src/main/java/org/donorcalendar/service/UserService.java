package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserProfile;
import org.donorcalendar.domain.UserStatus;
import org.donorcalendar.exception.NotFoundException;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {

    private final UserProfileDao userProfileDao;

    private final UserSecurityService userSecurityService;

    @Autowired
    public UserService(UserProfileDao userProfileDao, UserSecurityService userSecurityService) {
        this.userProfileDao = userProfileDao;
        this.userSecurityService = userSecurityService;
    }

    public UserProfile saveNewUser(User user) throws ValidationException {
        UserProfile userProfile = user.getUserProfile();
        if (isUserEmailAvailable(userProfile.getEmail())) {
            populateUserStatus(userProfile);
            userProfile = userProfileDao.saveNewUser(user.getUserProfile());
            user.setUserProfile(userProfile);
            userSecurityService.saveNewUserSecurityDetails(user);
            return userProfile;
        } else {
            throw new ValidationException("The email " + userProfile.getEmail() + " is already registered.");
        }
    }

    private void populateUserStatus(UserProfile userProfile) throws ValidationException {
        if (userProfile.getLastDonation() == null) {
            userProfile.setUserStatus(UserStatus.NEED_TO_DONATE);
        } else {
            LocalDate lastDonation = userProfile.getLastDonation();
            if (lastDonation.isAfter(LocalDate.now())) {
                throw new ValidationException("Last donation date can't be in the future.");
            } else {
                long daysSinceLastDonation = ChronoUnit.DAYS.between(lastDonation, LocalDate.now());
                userProfile.setUserStatus(UserStatus.getStatusByNumberOfDaysSinceLastDonation(daysSinceLastDonation));
            }
        }
    }

    public void updateExistingUser(UserProfile userProfile) throws ValidationException, NotFoundException {
        if (userProfileDao.exists(userProfile.getUserId())) {
            populateUserStatus(userProfile);
            userProfileDao.updateUser(userProfile);
        } else {
            throw new NotFoundException("User with id "+ userProfile.getUserId() + " could not be found.");
        }
    }

    public void updateUserPassword(Long userId, String unencryptedPassword) {
        userSecurityService.updateUserPassword(userId, unencryptedPassword);
    }

    private boolean isUserEmailAvailable(String userEmail) {
        return userProfileDao.findByEmail(userEmail) == null;
    }
}
