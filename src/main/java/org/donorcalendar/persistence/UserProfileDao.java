package org.donorcalendar.persistence;

import org.donorcalendar.domain.UserProfile;
import org.springframework.data.repository.query.Param;

public interface UserProfileDao {
    UserProfile saveNewUser(UserProfile userProfile);

    boolean exists(Long userId);

    UserProfile findOne(Long id);

    UserProfile findByEmail(@Param("email") String email);

    void updateUser(UserProfile userProfile);
}