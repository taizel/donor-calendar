package org.donorcalendar.persistence;

import java.util.HashMap;
import java.util.Optional;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.util.IdGenerator;
import org.springframework.data.repository.query.Param;

public class UserProfileDaoInMemoryImpl implements UserProfileDao {

    private final HashMap<Long, UserProfile> cache = new HashMap<>();

    @Override
    public UserProfile saveNewUser(UserProfile userProfile) {
        UserProfile userProfileCopy = new UserProfile(userProfile);
        if(needsToGenerateId(userProfileCopy.getUserId())) {
            userProfileCopy.setUserId(IdGenerator.generateNewId());
        }
        cache.put(userProfileCopy.getUserId(), userProfileCopy);
        return userProfile;
    }

    private boolean needsToGenerateId(Long userId) {
        return !(userId != null && userId > 0);
    }

    @Override
    public Optional<UserProfile> findById(Long id) {
        if(cache.containsKey(id)) {
            return Optional.of(cache.get(id));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserProfile> findByEmail(@Param("email") String email) {
        return cache.values().stream().filter(userProfile -> userProfile.getEmail().equals(email)).findFirst();
    }

    @Override
    public boolean existsById(Long userId) {
        return cache.containsKey(userId);
    }

    @Override
    public void updateUser(UserProfile userProfile) {
        cache.put(userProfile.getUserId(), userProfile);
    }
}