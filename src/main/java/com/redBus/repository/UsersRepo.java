package com.redBus.repository;

import com.redBus.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<Users, Long> {
    boolean existsByMobile(String mobile);
}
