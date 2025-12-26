package com.yourapp.services;

import com.yourapp.DAO.NotificationRepository;
import com.yourapp.dto.AuditResponseDto;
import com.yourapp.dto.NotificationDto;
import com.yourapp.model.Notification;
import com.yourapp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service backend pour la gestion des notifications
 * Communication directe avec la base de données via Repository
 */
@Service
@Transactional
@Slf4j
public class NotificationApiService {

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Récupérer toutes les notifications d'un utilisateur (triées par date)
     */
    public List<NotificationDto> getNotificationsByUser(Long userId) {
        log.info("📥 Fetching notifications for user {}", userId);

        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Compter les notifications non lues
     */
    public long getUnreadCount(Long userId) {
        long count = notificationRepository.countByUserIdAndReadFalse(userId);
        log.info("📊 User {} has {} unread notifications", userId, count);
        return count;
    }

    /**
     * Marquer une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
            log.info("✅ Notification {} marked as read", notificationId);
        });
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndReadFalse(userId);

        unreadNotifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unreadNotifications);

        log.info("✅ {} notifications marked as read for user {}",
                unreadNotifications.size(), userId);
    }

    /**
     * Créer une notification générique
     */
    public NotificationDto createNotification(User user, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(user.getId());
        notification.setUserName(user.getFullName());
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);
        log.info("✅ Notification created for user {}: {}", user.getId(), message);

        return convertToDto(notification);
    }

    /**
     * Notification: Audit terminé avec succès
     */
    public NotificationDto notifyAuditCompleted(User user, AuditResponseDto audit) {
        String message = String.format(
                "✅ Audit terminé avec succès pour le projet '%s'",
                audit.getProjectName() != null ? audit.getProjectName() : "Projet #" + audit.getProjectId()
        );

        Notification notification = new Notification();
        notification.setUserId(user.getId());
        notification.setUserName(user.getFullName());
        notification.setProjectId(audit.getProjectId());
        notification.setAuditId(audit.getId());
        notification.setMessage(message);
        notification.setType("AUDIT_COMPLETED");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);
        log.info("🔔 Audit completion notification created for user {}", user.getId());

        return convertToDto(notification);
    }

    /**
     * Notification: Audit échoué
     */
    public NotificationDto notifyAuditFailed(User user, AuditResponseDto audit, String reason) {
        String message = String.format(
                "❌ Échec de l'audit pour le projet '%s': %s",
                audit.getProjectName() != null ? audit.getProjectName() : "Projet #" + audit.getProjectId(),
                reason != null ? reason : "Erreur inconnue"
        );

        Notification notification = new Notification();
        notification.setUserId(user.getId());
        notification.setUserName(user.getFullName());
        notification.setProjectId(audit.getProjectId());
        notification.setAuditId(audit.getId());
        notification.setMessage(message);
        notification.setType("AUDIT_FAILED");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);
        log.info("🔔 Audit failure notification created for user {}", user.getId());

        return convertToDto(notification);
    }

    /**
     * Notification: Rapport généré
     */
    public NotificationDto notifyReportGenerated(User user, AuditResponseDto audit) {
        String message = String.format(
                "📄 Rapport généré pour l'audit du projet '%s'",
                audit.getProjectName() != null ? audit.getProjectName() : "Projet #" + audit.getProjectId()
        );

        Notification notification = new Notification();
        notification.setUserId(user.getId());
        notification.setUserName(user.getFullName());
        notification.setProjectId(audit.getProjectId());
        notification.setAuditId(audit.getId());
        notification.setMessage(message);
        notification.setType("REPORT_GENERATED");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);
        log.info("🔔 Report generation notification created for user {}", user.getId());

        return convertToDto(notification);
    }

    /**
     * Supprimer une notification
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        log.info("🗑️ Notification {} deleted", notificationId);
    }

    /**
     * Supprimer toutes les notifications d'un utilisateur
     */
    public void clearAllNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notificationRepository.deleteAll(notifications);
        log.info("🗑️ All notifications cleared for user {}", userId);
    }

    /**
     * Récupérer les notifications récentes (30 derniers jours)
     */
    public List<NotificationDto> getRecentNotifications(Long userId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<Notification> notifications = notificationRepository
                .findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, thirtyDaysAgo);

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Nettoyer les anciennes notifications lues (> 90 jours)
     */
    public void cleanupOldNotifications() {
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        notificationRepository.deleteByReadTrueAndCreatedAtBefore(ninetyDaysAgo);
        log.info("🧹 Old read notifications cleaned up");
    }

    /**
     * Convertir une entité Notification en NotificationDto
     */
    private NotificationDto convertToDto(Notification entity) {
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setUserName(entity.getUserName());
        dto.setProjectId(entity.getProjectId());
        dto.setAuditId(entity.getAuditId());
        dto.setType(entity.getType());
        dto.setMessage(entity.getMessage());
        dto.setRead(entity.isRead());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}