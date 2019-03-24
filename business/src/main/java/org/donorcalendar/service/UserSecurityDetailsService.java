package org.donorcalendar.service;

import org.donorcalendar.model.User;

/***
 * An implementation of this interface is necessary for management of user security details and it's mandatory for the
 * business module to work properly.
 */
public interface UserSecurityDetailsService {
    void saveNewUserSecurityDetails(User user);

    void updateUserPassword(Long userId, String newPassword);
}
