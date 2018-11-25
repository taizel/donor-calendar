package org.donorcalendar.persistence;

import org.donorcalendar.model.UserSecurityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityDetailsDaoImpl implements UserSecurityDetailsDao {

    private final UserSecurityDetailsRepository userSecurityDetailsRepository;

    @Autowired
    public UserSecurityDetailsDaoImpl(UserSecurityDetailsRepository userSecurityDetailsRepository) {
        this.userSecurityDetailsRepository = userSecurityDetailsRepository;
    }

    @Override
    public void saveNewUserSecurityDetails(Long userId, UserSecurityDetails userSecurityDetails) {
        UserSecurityDetailsEntity userSecurityDetailsEntity = new UserSecurityDetailsEntity();
        userSecurityDetailsEntity.setUserId(userId);
        userSecurityDetailsEntity.setPassword(userSecurityDetails.getPassword());
        userSecurityDetailsRepository.save(userSecurityDetailsEntity);
    }

    @Override
    public UserSecurityDetails findByUserId(Long userId) {
        return userSecurityDetailsRepository.findByUserId(userId).getUserSecurityDetails();
    }

    @Override
    public void updateUserPassword(Long userId, String newEncodedPassword) {
        UserSecurityDetailsEntity userSecurityDetailsEntity = userSecurityDetailsRepository.findByUserId(userId);
        userSecurityDetailsEntity.setPassword(newEncodedPassword);
        userSecurityDetailsRepository.save(userSecurityDetailsEntity);
    }
}
