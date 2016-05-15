package org.donorcalendar.persistence;

import org.donorcalendar.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.nextReminder <= CURRENT_DATE")
    List<User> findUsersToRemind();
}
