package org.donorcalendar.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserSecurityDetailsRepository extends CrudRepository<UserSecurityDetailsEntity, Long> {
    UserSecurityDetailsEntity findByUserId(@Param("userId") Long userId);
}