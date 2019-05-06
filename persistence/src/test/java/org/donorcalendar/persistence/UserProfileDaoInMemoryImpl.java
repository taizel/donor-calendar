package org.donorcalendar.persistence;

import org.donorcalendar.model.UserProfile;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("test")
public class UserProfileDaoInMemoryImpl implements UserProfileDao {

    private final HashMap<Long, UserProfile> cache = new HashMap<>();

    @Override
    public UserProfile saveNewUser(UserProfile userProfile) {
        UserProfile userProfileCopy = new UserProfile(userProfile);
        cache.put(userProfileCopy.getUserId(), userProfileCopy);
        return userProfileCopy;
    }

    @Override
    public List<UserProfile> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public Optional<UserProfile> findById(Long id) {
        if (cache.containsKey(id)) {
            return Optional.of(new UserProfile(cache.get(id)));
        }
        return Optional.empty();
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

    @Override
    public List<UserProfile> findUsersToRemind() {
        LocalDate currentLocalDate = LocalDate.now();
        return cache.values().stream()
                .filter(user -> user.getNextReminder() != null && user.getNextReminder().isBefore(currentLocalDate))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        cache.clear();
    }
}