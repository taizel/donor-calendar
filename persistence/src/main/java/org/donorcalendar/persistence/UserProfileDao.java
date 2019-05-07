package org.donorcalendar.persistence;

import org.donorcalendar.model.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserProfileDao {
    UserProfile saveNewUser(UserProfile userProfile);

    List<UserProfile> findAll();

    boolean existsById(Long userId);

    Optional<UserProfile> findById(Long id);

    Optional<UserProfile> findByEmail(String email);

    List<UserProfile> findUsersToRemind();

    void updateUser(UserProfile userProfile);
}