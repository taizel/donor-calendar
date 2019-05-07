package org.donorcalendar.persistence;

import org.donorcalendar.model.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("production")
public class UserCredentialsDaoImpl implements UserCredentialsDao {

    private final UserCredentialsRepository userCredentialsRepository;

    @Autowired
    public UserCredentialsDaoImpl(UserCredentialsRepository userCredentialsRepository) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public UserCredentials saveNewUserCredentials(Long userId, UserCredentials userCredentials) {
        return savePassword(userId, userCredentials.getPassword()).getUserCredentials();
    }

    private UserCredentialsEntity savePassword(Long userId, String encodedPassword) {
        Optional<UserCredentialsEntity> optionalEntity = userCredentialsRepository.findByUserId(userId);
        if (optionalEntity.isPresent()) {
            UserCredentialsEntity userCredentialsEntity = optionalEntity.get();
            userCredentialsEntity.setPassword(encodedPassword);
            return userCredentialsRepository.save(userCredentialsEntity);
        } else {
            return userCredentialsRepository.save(new UserCredentialsEntity(userId, new UserCredentials(encodedPassword)));
        }
    }

    @Override
    public Optional<UserCredentials> findByUserId(Long userId) {
        Optional<UserCredentialsEntity> userCredentialsEntity = userCredentialsRepository.findByUserId(userId);
        return userCredentialsEntity.map(UserCredentialsEntity::getUserCredentials);
    }

    @Override
    public void saveUserPassword(Long userId, String newEncodedPassword) {
        savePassword(userId, newEncodedPassword);
    }
}
