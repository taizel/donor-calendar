package org.donorcalendar.security;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserProfileDao userProfileDao;
    private final UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Autowired
    public UserDetailsServiceImpl(UserProfileDao userProfileDao, UserSecurityDetailsRepository userSecurityDetailsRepository) {
        this.userProfileDao = userProfileDao;
        this.userSecurityDetailsRepository = userSecurityDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserProfile> optionalUserProfile = userProfileDao.findByEmail(email);
        if (optionalUserProfile.isPresent()) {
            UserProfile userProfile = optionalUserProfile.get();
            UserSecurityDetailsEntity userSecurityDetails = userSecurityDetailsRepository.findByUserId(userProfile.getUserId());
            return new UserAuthenticationDetails(userProfile, userSecurityDetails.getPassword());
        }
        throw new UsernameNotFoundException("No user registered with the email '" + email + "'");
    }
}
