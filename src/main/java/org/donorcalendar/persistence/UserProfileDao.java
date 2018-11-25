package org.donorcalendar.persistence;

import org.donorcalendar.model.UserProfile;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProfileDao {
    UserProfile saveNewUser(UserProfile userProfile);

    List<UserProfile> findAll();

    boolean existsById(Long userId);

    Optional<UserProfile> findById(Long id);

    Optional<UserProfile> findByEmail(@Param("email") String email);

    void updateUser(UserProfile userProfile);

    List<UserProfile> findUsersToRemind();
}