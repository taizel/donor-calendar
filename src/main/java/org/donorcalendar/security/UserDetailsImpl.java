package org.donorcalendar.security;

import org.donorcalendar.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl extends org.springframework.security.core.userdetails.User implements UserDetails {

    private User user;

    public UserDetailsImpl(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getName(), user.getPassword(), authorities);
        this.user = user;
    }

    public UserDetailsImpl(User user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getName(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

//    The commented ones doesn't need to be implemented because userdetails.User already contains an implementation it.
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return AuthorityUtils.createAuthorityList("ROLE_USER");
//    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

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
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
}
