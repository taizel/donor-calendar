package org.donorcalendar.persistence;

import org.donorcalendar.model.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCredentialsDaoImpl implements UserCredentialsDao {

    private final UserCredentialsRepository userCredentialsRepository;

    @Autowired
    public UserCredentialsDaoImpl(UserCredentialsRepository userCredentialsRepository) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public UserCredentials saveNewUserCredentials(Long userId, UserCredentials userCredentials) {
        UserCredentialsEntity userCredentialsEntity = new UserCredentialsEntity();
        userCredentialsEntity.setUserId(userId);
        userCredentialsEntity.setPassword(userCredentials.getPassword());
        return userCredentialsRepository.save(userCredentialsEntity).getUserCredentials();
    }

    @Override
    public UserCredentials findByUserId(Long userId) {
        return userCredentialsRepository.findByUserId(userId).getUserCredentials();
    }

    @Override
    public void updateUserPassword(Long userId, String newEncodedPassword) {
        UserCredentialsEntity userCredentialsEntity = userCredentialsRepository.findByUserId(userId);
        userCredentialsEntity.setPassword(newEncodedPassword);
        userCredentialsRepository.save(userCredentialsEntity);
    }
}
