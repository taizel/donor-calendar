package org.donorcalendar.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends CrudRepository<UserProfileEntity, Long> {

    Optional<UserProfileEntity> findByEmail(String email);

    @Query("SELECT u FROM UserProfileEntity u WHERE u.nextReminder <= CURRENT_DATE")
    List<UserProfileEntity> findUsersToRemind();
}
