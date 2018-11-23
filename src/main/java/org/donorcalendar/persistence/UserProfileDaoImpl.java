package org.donorcalendar.persistence;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class UserProfileDaoImpl implements UserProfileDao {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileDaoImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public UserProfile saveNewUser(UserProfile userProfile) {
        UserProfileEntity userProfileEntity = convertUserToUserEntity(userProfile);
        if(needsToGenerateId(userProfileEntity.getUserId())) {
            userProfileEntity.setUserId(IdGenerator.generateNewId());
        }
        userProfileEntity = userProfileRepository.save(userProfileEntity);
        return userProfileEntity.getUserDetails();
    }

    private boolean needsToGenerateId(Long userId) {
        return !(userId != null && userId > 0);
    }

    @Override
    public Optional<UserProfile> findById(Long id) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findById(id);
        return userProfileEntity.map(UserProfileEntity::getUserDetails);
    }

    @Override
    public Optional<UserProfile> findByEmail(@Param("email") String email) {
        UserProfileEntity userProfileEntity = userProfileRepository.findByEmail(email);
        return userProfileEntity != null ? Optional.of(userProfileEntity.getUserDetails()) : Optional.empty();
    }

    @Override
    public boolean existsById(Long userId) {
        return userProfileRepository.existsById(userId);
    }

    @Override
    public void updateUser(UserProfile userProfile){
        UserProfileEntity userProfileEntity = convertUserToUserEntity(userProfile);
        userProfileRepository.save(userProfileEntity);
    }


    private UserProfileEntity convertUserToUserEntity(UserProfile userProfile){
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setUserId(userProfile.getUserId());
        userProfileEntity.setName(userProfile.getName());
        userProfileEntity.setEmail(userProfile.getEmail());
        userProfileEntity.setBloodType(userProfile.getBloodType());
        userProfileEntity.setDaysBetweenReminders(userProfile.getDaysBetweenReminders());
        userProfileEntity.setLastDonation(userProfile.getLastDonation());
        userProfileEntity.setNextReminder(userProfile.getNextReminder());
        userProfileEntity.setUserStatus(userProfile.getUserStatus());
        return userProfileEntity;
    }
}