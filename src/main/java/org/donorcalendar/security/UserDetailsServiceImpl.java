package org.donorcalendar.security;

import org.donorcalendar.domain.UserProfile;
import org.donorcalendar.persistence.UserRepository;
import org.donorcalendar.persistence.UserSecurityDetailsEntity;
import org.donorcalendar.persistence.UserSecurityDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserProfile user = userRepository.findByEmail(email).getUserDetails();
        if (user == null) {
            throw new UsernameNotFoundException("UserProfile " + email + " not found");
        } else {
            UserSecurityDetailsEntity userSecurityDetails = userSecurityDetailsRepository.findByUserId(user.getUserId());
            UserDetailsImpl userDetails = new UserDetailsImpl(user, userSecurityDetails.getPassword(),true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_USER"));
            return userDetails;
        }
    }
}
