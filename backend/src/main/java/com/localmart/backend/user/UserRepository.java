package com.localmart.backend.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);   // used at login

    boolean existsByPhone(String phone);        // used at register (duplicate check)
}
