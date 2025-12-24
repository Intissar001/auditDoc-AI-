package com.yourapp.DAO;

import com.yourapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Required for AuthenticationService.login and requestPasswordReset
    Optional<User> findByEmail(String email);

    // Required for AuthenticationService.register
    boolean existsByEmail(String email);
}