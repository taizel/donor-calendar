package org.donorcalendar.security;

import org.donorcalendar.domain.UserProfile;
import org.donorcalendar.persistence.UserProfileRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
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
        //TODO check if this is called on every call to the end points (performance concern)
        UserProfile user = userProfileRepository.findByEmail(email).getUserDetails();
        if (user == null) {
            throw new UsernameNotFoundException("UserProfile " + email + " not found");
        } else {
            UserSecurityDetailsEntity userSecurityDetails = userSecurityDetailsRepository.findByUserId(user.getUserId());
            UserAuthenticationDetails userDetails = new UserAuthenticationDetails(user, userSecurityDetails.getPassword());
            return userDetails;
        }
    }
}
