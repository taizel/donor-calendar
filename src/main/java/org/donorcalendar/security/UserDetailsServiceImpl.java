package org.donorcalendar.security;

import org.donorcalendar.domain.User;
import org.donorcalendar.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<User> userList = userRepository.findByEmail(email);
        if (userList.isEmpty()) {
            throw new UsernameNotFoundException("User " + email + " not found");
        } else {
            User user = userList.get(0);
            UserDetailsImpl userDetails = new UserDetailsImpl(user, true, true, true, true, AuthorityUtils.createAuthorityList("ROLE_USER"));
            return userDetails;
        }
    }
}
