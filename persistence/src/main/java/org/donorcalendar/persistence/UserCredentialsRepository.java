package org.donorcalendar.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserCredentialsRepository extends CrudRepository<UserCredentialsEntity, Long> {
    Optional<UserCredentialsEntity> findByUserId(@Param("userId") Long userId);
}