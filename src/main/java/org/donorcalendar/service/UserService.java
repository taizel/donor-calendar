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
        UserProfile userProfile = userProfileDao.saveNewUser(user.getUserProfile());
        user.setUserProfile(userProfile);
        userSecurityService.saveNewUserSecurityDetails(user);
        return userProfile;
    }

    public void updateExistingUser(UserProfile userProfile) throws ValidationException {
        if(userProfileDao.exists(userProfile.getUserId())){
            userProfileDao.updateUser(userProfile);
        }
    }

    public void updateUserPassword(Long userId, String unencryptedPassword){
        userSecurityService.updateUserPassword(userId, unencryptedPassword);
    }
}
