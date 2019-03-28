package org.donorcalendar.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserCredentialsRepository extends CrudRepository<UserCredentialsEntity, Long> {
    UserCredentialsEntity findByUserId(@Param("userId") Long userId);
}