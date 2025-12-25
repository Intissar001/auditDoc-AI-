package com.yourapp.DAO;

import com.yourapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouver un utilisateur par email
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifier si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Trouver des utilisateurs par rôle
     */
    List<User> findByRole(String role);

    /**
     * Trouver des utilisateurs actifs (avec alertes activées)
     */
    List<User> findByEmailAlertsTrue();

    /**
     * Trouver des utilisateurs avec rappels d'audit activés
     */
    List<User> findByAuditRemindersTrue();
}