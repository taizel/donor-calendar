package org.donorcalendar.persistence;

import org.donorcalendar.model.UserProfile;
import org.donorcalendar.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        if(needsToGenerateId(userProfile.getUserId())) {
            userProfile.setUserId(IdGenerator.generateNewId());
        }
        UserProfileEntity userProfileEntity = userProfileRepository.save(convertUserToUserEntity(userProfile));
        return userProfileEntity.getUserDetails();
    }

    private boolean needsToGenerateId(Long userId) {
        return !(userId != null && userId > 0);
    }

    @Override
    public UserProfile findOne(Long id) {
        UserProfileEntity userProfileEntity = userProfileRepository.findOne(id);
        return userProfileEntity.getUserDetails();
    }

    @Override
    public UserProfile findByEmail(@Param("email") String email) {
        UserProfileEntity userProfileEntity = userProfileRepository.findByEmail(email);
        return userProfileEntity != null ? userProfileEntity.getUserDetails() : null;
    }

    @Override
    public boolean exists(Long userId) {
        return userProfileRepository.exists(userId);
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