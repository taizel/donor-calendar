package org.donorcalendar.service;

import org.donorcalendar.domain.User;
import org.donorcalendar.domain.UserProfile;
import org.donorcalendar.domain.UserSecurityDetails;
import org.donorcalendar.persistence.UserDao;
import org.donorcalendar.exception.ValidationException;
import org.donorcalendar.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSecurityService userSecurityService;

    public UserProfile saveNewUser(User user) throws ValidationException {
        UserProfile userProfile = userDao.saveNewUser(user.getUserProfile());
        user.setUserProfile(userProfile);
        UserSecurityDetails userSecurityDetails = userSecurityService.saveNewUserSecurityDetails(user);
        user.setUserSecurity(userSecurityDetails);
        return userProfile;
    }

    public void updateExistingUser(UserProfile userProfile) throws ValidationException {
        if(userDao.exists(userProfile.getUserId())){
            userDao.updateUser(userProfile);
        }
    }
}
