package org.donorcalendar.service;

import org.donorcalendar.model.User;
import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserStatus;
import org.donorcalendar.model.NotFoundException;
import org.donorcalendar.model.ValidationException;
import org.donorcalendar.persistence.UserProfileDao;
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
        UserProfile userProfile = new UserProfile(user.getUserProfile());
        if (isUserEmailAvailable(userProfile.getEmail())) {
            populateUserStatus(userProfile);
            userProfile = userProfileDao.saveNewUser(userProfile);
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
                userProfile.setUserStatus(UserStatus.fromNumberOfElapsedDaysSinceLastDonation(daysSinceLastDonation));
            }
        }
    }

    public void updateUserProfile(UserProfile userProfile) throws ValidationException, NotFoundException {
        validateIfUserExists(userProfile.getUserId());
        UserProfile userToUpdate = new UserProfile(userProfile);
        populateUserStatus(userToUpdate);
        userProfileDao.updateUser(userToUpdate);
    }

    public void updateUserPassword(Long userId, String unencryptedPassword) throws ValidationException, NotFoundException {
        if (unencryptedPassword != null && !unencryptedPassword.isEmpty()) {
            validateIfUserExists(userId);
            userSecurityService.updateUserPassword(userId, unencryptedPassword);
        } else {
            throw new ValidationException("New password cannot be empty.");
        }
    }

    private void validateIfUserExists(Long userId) throws NotFoundException {
        if (!userProfileDao.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " could not be found.");
        }
    }

    private boolean isUserEmailAvailable(String userEmail) {
        return !userProfileDao.findByEmail(userEmail).isPresent();
    }
}