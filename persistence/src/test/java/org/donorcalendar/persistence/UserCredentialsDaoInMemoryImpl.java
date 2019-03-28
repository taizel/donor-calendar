package org.donorcalendar.persistence;

import org.donorcalendar.model.UserCredentials;


import java.util.HashMap;

public class UserCredentialsDaoInMemoryImpl implements UserCredentialsDao {

    private final HashMap<Long, UserCredentials> cache = new HashMap<>();

    @Override
    public UserCredentials saveNewUserCredentials(Long userId, UserCredentials userCredentials) {
        cache.put(userId, new UserCredentials(userCredentials));
        return new UserCredentials(userCredentials);
    }

    @Override
    public UserCredentials findByUserId(Long userId) {
        return new UserCredentials(cache.get(userId));
    }

    @Override
    public void updateUserPassword(Long userId, String encodedNewPassword) {
        cache.get(userId).setPassword(encodedNewPassword);
    }
}
