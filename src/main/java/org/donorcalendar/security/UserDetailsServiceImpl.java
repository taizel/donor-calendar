package org.donorcalendar.security;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.model.UserSecurityDetails;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.persistence.UserSecurityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserProfileDao userProfileDao;
    private final UserSecurityDao userSecurityDao;

    @Autowired
    public UserDetailsServiceImpl(UserProfileDao userProfileDao, UserSecurityDao userSecurityDao) {
        this.userProfileDao = userProfileDao;
        this.userSecurityDao = userSecurityDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserProfile> optionalUserProfile = userProfileDao.findByEmail(email);
        if (optionalUserProfile.isPresent()) {
            UserProfile userProfile = optionalUserProfile.get();
            UserSecurityDetails userSecurityDetails = userSecurityDao.findByUserId(userProfile.getUserId());
            return new UserAuthenticationDetails(userProfile, userSecurityDetails.getPassword());
        }
        throw new UsernameNotFoundException("No user registered with the email '" + email + "'");
    }
}
