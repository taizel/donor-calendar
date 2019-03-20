package org.donorcalendar.persistence;

import org.donorcalendar.model.UserSecurityDetails;

public interface UserSecurityDetailsDao {
    UserSecurityDetails saveNewUserSecurityDetails(Long userId, UserSecurityDetails userSecurityDetails);

    UserSecurityDetails findByUserId(Long userId);

    void updateUserPassword(Long userId, String encodedNewPassword);
}
