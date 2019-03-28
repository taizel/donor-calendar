package org.donorcalendar.security;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserCredentials;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.persistence.UserCredentialsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("UserDetailsService")
public class UserSecurityDetailsService implements UserDetailsService {

    private final UserProfileDao userProfileDao;
    private final UserCredentialsDao userCredentialsDao;

    @Autowired
    public UserSecurityDetailsService(UserProfileDao userProfileDao, UserCredentialsDao userCredentialsDao) {
        this.userProfileDao = userProfileDao;
        this.userCredentialsDao = userCredentialsDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<UserProfile> optionalUserProfile = userProfileDao.findByEmail(email);
        if (optionalUserProfile.isPresent()) {
            UserProfile userProfile = optionalUserProfile.get();
            UserCredentials userCredentials = userCredentialsDao.findByUserId(userProfile.getUserId());
            return new UserSecurityDetails(userProfile, userCredentials.getPassword());
        }
        throw new UsernameNotFoundException("No user registered with the email '" + email + "'");
    }
}
