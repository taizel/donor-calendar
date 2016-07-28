package org.donorcalendar.security;

import org.donorcalendar.domain.UserProfile;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserDetailsImpl extends org.springframework.security.core.userdetails.User implements org.springframework.security.core.userdetails.UserDetails {

    private final UserProfile userProfile;

    private final String password;

    public UserDetailsImpl(UserProfile userProfile, String password, Collection<? extends GrantedAuthority> authorities) {
        super(userProfile.getName(), password, authorities);
        this.userProfile = userProfile;
        this.password = password;
    }

    public UserDetailsImpl(UserProfile userProfile, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(userProfile.getName(), password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userProfile = userProfile;
        this.password = password;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

//    The commented ones doesn't need to be implemented because {org.springframework.security.core.userdetails.UserProfile} already implements it.
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return AuthorityUtils.createAuthorityList("ROLE_USER");
//    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userProfile.getEmail();
    }

//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
}
