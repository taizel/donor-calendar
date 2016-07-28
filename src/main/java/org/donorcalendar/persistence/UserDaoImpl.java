package org.donorcalendar.persistence;

import org.donorcalendar.domain.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class UserDaoImpl implements UserDao {


    private final UserRepository userRepository;

    private final UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Autowired
    public UserDaoImpl(UserRepository userRepository, UserSecurityDetailsRepository userSecurityDetailsRepository) {
        this.userRepository = userRepository;
        this.userSecurityDetailsRepository = userSecurityDetailsRepository;
    }

    @Override
    public UserProfile saveNewUser(UserProfile userProfile) {
        UserProfileEntity userProfileEntity = userRepository.save(convertUserToUserEntity(userProfile));
        return userProfileEntity.getUserDetails();
    }

    @Override
    public UserProfile findOne(Long id) {
        UserProfileEntity userProfileEntity = userRepository.findOne(id);
        return userProfileEntity.getUserDetails();
    }

    @Override
    public UserProfile findByEmail(@Param("email") String email) {
        UserProfileEntity userProfileEntity = userRepository.findByEmail(email);
        return userProfileEntity.getUserDetails();
    }

    @Override
    public boolean exists(Long userId) {
        return userRepository.exists(userId);
    }

    @Override
    public void updateUser(UserProfile userProfile){
        UserProfileEntity userProfileEntity = convertUserToUserEntity(userProfile);
        userRepository.save(userProfileEntity);
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