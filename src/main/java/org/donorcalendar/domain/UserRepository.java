package org.donorcalendar.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByEmail(@Param("email") String email);
}
