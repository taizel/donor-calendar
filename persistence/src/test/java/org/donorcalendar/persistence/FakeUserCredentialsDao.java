package org.donorcalendar.persistence;

import org.donorcalendar.model.UserCredentials;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Component
@Profile("test")
public class FakeUserCredentialsDao implements UserCredentialsDao {

    private final HashMap<Long, UserCredentials> cache = new HashMap<>();

    @Override
    public UserCredentials saveNewUserCredentials(Long userId, UserCredentials userCredentials) {
        cache.put(userId, new UserCredentials(userCredentials.getPassword()));
        return userCredentials;
    }

    @Override
    public Optional<UserCredentials> findByUserId(Long userId) {
        if (cache.containsKey(userId)) {
            return Optional.of(cache.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public void saveUserPassword(Long userId, String encodedNewPassword) {
        cache.put(userId, new UserCredentials(encodedNewPassword));
    }

    public void deleteAll() {
        cache.clear();
    }
}
