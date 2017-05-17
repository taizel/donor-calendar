package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserProfile;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        //TODO should a dedicated method to check email availability?
        if(isUserEmailAvailable(userProfile.getEmail())){
            userProfile = userProfileDao.saveNewUser(user.getUserProfile());
            user.setUserProfile(userProfile);
            userSecurityService.saveNewUserSecurityDetails(user);
            return userProfile;
        } else {
            throw new ValidationException("The email " + userProfile.getEmail() + " is already registered.");
        }
    }

    public void updateExistingUser(UserProfile userProfile) throws ValidationException {
        //TODO throw exception if user doesn't exist, add appropriate unity test
        if(userProfileDao.exists(userProfile.getUserId())) {
            userProfileDao.updateUser(userProfile);
        }
    }

    public void updateUserPassword(Long userId, String unencryptedPassword){
        userSecurityService.updateUserPassword(userId, unencryptedPassword);
    }

    private boolean isUserEmailAvailable(String userEmail){
        return userProfileDao.findByEmail(userEmail) == null;
    }
}
