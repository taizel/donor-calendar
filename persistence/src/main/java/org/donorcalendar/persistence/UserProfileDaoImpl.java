package org.donorcalendar.persistence;

import org.donorcalendar.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("production")
public class UserProfileDaoImpl implements UserProfileDao {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileDaoImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile saveNewUser(UserProfile userProfile) {
        UserProfileEntity userProfileEntity = new UserProfileEntity(userProfile);
        userProfileEntity = userProfileRepository.save(userProfileEntity);
        return userProfileEntity.getUserProfile();
    }

    @Override
    public List<UserProfile> findAll() {
        List<UserProfile> usersList = new ArrayList<>();
        userProfileRepository.findAll().iterator().forEachRemaining(
                userProfileEntity -> usersList.add(userProfileEntity.getUserProfile()));
        return usersList;
    }

    @Override
    public Optional<UserProfile> findById(Long id) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findById(id);
        return userProfileEntity.map(UserProfileEntity::getUserProfile);
    }

    @Override
    public Optional<UserProfile> findByEmail(String email) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByEmail(email);
        return userProfileEntity.map(UserProfileEntity::getUserProfile);
    }

    @Override
    public boolean existsById(Long userId) {
        return userProfileRepository.existsById(userId);
    }

    @Override
    public void updateUser(UserProfile userProfile) {
        UserProfileEntity userProfileEntity = new UserProfileEntity(userProfile);
        userProfileRepository.save(userProfileEntity);
    }

    @Override
    public List<UserProfile> findUsersToRemind() {
        return userProfileRepository.findUsersToRemind().stream().map(UserProfileEntity::getUserProfile).collect(Collectors.toList());
    }

    @Override
    public void deleteAll() {
        userProfileRepository.deleteAll();
    }
}