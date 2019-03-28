package org.donorcalendar.persistence;

import org.donorcalendar.model.UserCredentials;

public interface UserCredentialsDao {
    UserCredentials saveNewUserCredentials(Long userId, UserCredentials userCredentials);

    UserCredentials findByUserId(Long userId);

    void updateUserPassword(Long userId, String encodedNewPassword);
}
