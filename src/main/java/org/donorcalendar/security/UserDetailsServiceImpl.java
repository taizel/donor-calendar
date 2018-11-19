package org.donorcalendar.security;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserProfileRepository userProfileRepository;
    private final UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Autowired
    public UserDetailsServiceImpl(UserProfileRepository userProfileRepository, UserSecurityDetailsRepository userSecurityDetailsRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userSecurityDetailsRepository = userSecurityDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserProfile user = userProfileRepository.findByEmail(email).getUserDetails();
        if (user == null) {
            throw new UsernameNotFoundException("No user registered with the email '" + email + "'");
        }
        UserSecurityDetailsEntity userSecurityDetails = userSecurityDetailsRepository.findByUserId(user.getUserId());
        return new UserAuthenticationDetails(user, userSecurityDetails.getPassword());
    }
}
