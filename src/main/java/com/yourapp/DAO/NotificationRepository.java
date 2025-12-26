package com.yourapp.DAO;

import com.yourapp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a user, ordered by creation date (newest first)
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find notifications for a user
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Find unread notifications for a user
     */
    List<Notification> findByUserIdAndReadFalse(Long userId);

    /**
     * Count unread notifications for a user
     */
    long countByUserIdAndReadFalse(Long userId);

    /**
     * Find notifications by type for a user
     */
    List<Notification> findByUserIdAndType(Long userId, String type);

    /**
     * Delete old read notifications
     */
    void deleteByReadTrueAndCreatedAtBefore(LocalDateTime cutoffDate);

    /**
     * Find recent notifications (last N days)
     */
    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(
            Long userId, LocalDateTime after);
}