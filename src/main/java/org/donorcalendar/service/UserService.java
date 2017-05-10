package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserProfile;
import org.donorcalendar.persistence.UserDao;
import org.donorcalendar.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;

    private final UserSecurityService userSecurityService;

    @Autowired
    public UserService(UserDao userDao, UserSecurityService userSecurityService) {
        this.userDao = userDao;
        this.userSecurityService = userSecurityService;
    }

    public UserProfile saveNewUser(User user) throws ValidationException {
        UserProfile userProfile = userDao.saveNewUser(user.getUserProfile());
        user.setUserProfile(userProfile);
        userSecurityService.saveNewUserSecurityDetails(user);
        return userProfile;
    }

    public void updateExistingUser(UserProfile userProfile) throws ValidationException {
        if(userDao.exists(userProfile.getUserId())){
            userDao.updateUser(userProfile);
        }
    }

    public void updateUserPassword(Long userId, String unencryptedPassword){
        userSecurityService.updateUserPassword(userId, unencryptedPassword);
    }
}
