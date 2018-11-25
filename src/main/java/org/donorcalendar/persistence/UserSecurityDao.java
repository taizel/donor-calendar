package org.donorcalendar.persistence;

import org.donorcalendar.model.UserSecurityDetails;

public interface UserSecurityDao {
    void saveNewUserSecurityDetails(Long userId, UserSecurityDetails userSecurityDetails);

    UserSecurityDetails findByUserId(Long userId);

    void updateUserPassword(Long userId, String encodedNewPassword);
}