package org.donorcalendar.persistence;

import org.donorcalendar.domain.UserProfile;
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
        UserProfileEntity userProfileEntity = userProfileRepository.save(convertUserToUserEntity(userProfile));
        return userProfileEntity.getUserDetails();
    }

    @Override
    public UserProfile findOne(Long id) {
        UserProfileEntity userProfileEntity = userProfileRepository.findOne(id);
        return userProfileEntity.getUserDetails();
    }

    @Override
    public UserProfile findByEmail(@Param("email") String email) {
        UserProfileEntity userProfileEntity = userProfileRepository.findByEmail(email);
        return userProfileEntity.getUserDetails();
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
        return userProfileEntity;
    }
}