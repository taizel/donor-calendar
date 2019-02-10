package org.donorcalendar.persistence;

import org.donorcalendar.model.UserSecurityDetails;


import java.util.HashMap;

public class UserSecurityDetailsDaoInMemoryImpl implements UserSecurityDetailsDao {

    private final HashMap<Long, UserSecurityDetails> cache = new HashMap<>();

    @Override
    public void saveNewUserSecurityDetails(Long userId, UserSecurityDetails userSecurityDetails) {
        cache.put(userId, new UserSecurityDetails(userSecurityDetails));
    }

    @Override
    public UserSecurityDetails findByUserId(Long userId) {
        return new UserSecurityDetails(cache.get(userId));
    }

    @Override
    public void updateUserPassword(Long userId, String encodedNewPassword) {
        cache.get(userId).setPassword(encodedNewPassword);
    }
}
