package org.donorcalendar.persistence;

import org.donorcalendar.model.UserCredentials;

import java.util.Optional;

public interface UserCredentialsDao {
    UserCredentials saveNewUserCredentials(Long userId, UserCredentials userCredentials);

    Optional<UserCredentials> findByUserId(Long userId);

    void saveUserPassword(Long userId, String encodedNewPassword);

    void deleteAll();
}
