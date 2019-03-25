package org.donorcalendar.service;

import org.donorcalendar.model.User;

public interface UserSecurityDetailsService {
    void saveNewUserSecurityDetails(User user);

    void updateUserPassword(Long userId, String newPassword);
}
