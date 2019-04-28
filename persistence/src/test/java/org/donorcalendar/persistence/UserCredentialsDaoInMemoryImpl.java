package org.donorcalendar.persistence;

import org.donorcalendar.model.UserCredentials;


import java.util.HashMap;
import java.util.Optional;

public class UserCredentialsDaoInMemoryImpl implements UserCredentialsDao {

    private final HashMap<Long, UserCredentials> cache = new HashMap<>();

    @Override
    public UserCredentials saveNewUserCredentials(Long userId, UserCredentials userCredentials) {
        cache.put(userId, new UserCredentials(userCredentials));
        return new UserCredentials(userCredentials);
    }

    @Override
    public Optional<UserCredentials> findByUserId(Long userId) {
        if (cache.containsKey(userId)) {
            return Optional.of(new UserCredentials(cache.get(userId)));
        }
        return Optional.empty();
    }

    @Override
    public void saveUserPassword(Long userId, String encodedNewPassword) {
        cache.get(userId).setPassword(encodedNewPassword);
    }

    public void deleteAll() {
        cache.clear();
    }
}
