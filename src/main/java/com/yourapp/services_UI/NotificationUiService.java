package com.yourapp.services_UI;

import com.yourapp.dto.AuditResponseDto;
import com.yourapp.dto.NotificationDto;
import com.yourapp.model.User;
import com.yourapp.services.NotificationApiService;
import com.yourapp.utils.SessionManager;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service UI pour les notifications - Interface entre JavaFX et le backend
 * Utilise uniquement des DTOs pour communiquer avec le frontend
 */
@Service
@Slf4j
public class NotificationUiService {

    @Autowired
    private NotificationApiService notificationApiService;

    /**
     * Récupérer toutes les notifications de l'utilisateur connecté
     */
    public List<NotificationDto> getAllNotifications() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.warn("⚠️ No user logged in, returning empty notifications");
            return Collections.emptyList();
        }

        try {
            return notificationApiService.getNotificationsByUser(userId);
        } catch (Exception e) {
            log.error("❌ Error fetching notifications", e);
            return Collections.emptyList();
        }
    }

    /**
     * Récupérer le nombre de notifications non lues
     */
    public long getUnreadCount() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return 0;
        }

        try {
            return notificationApiService.getUnreadCount(userId);
        } catch (Exception e) {
            log.error("❌ Error fetching unread count", e);
            return 0;
        }
    }

    /**
     * Marquer une notification comme lue
     */
    public void markAsRead(Long notificationId) {
        try {
            notificationApiService.markAsRead(notificationId);
            log.info("✅ Notification {} marked as read", notificationId);
        } catch (Exception e) {
            log.error("❌ Error marking notification as read", e);
        }
    }

    /**
     * Marquer toutes les notifications comme lues
     */
    public void markAllAsRead() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.warn("⚠️ No user logged in");
            return;
        }

        try {
            notificationApiService.markAllAsRead(userId);
            log.info("✅ All notifications marked as read for user {}", userId);
        } catch (Exception e) {
            log.error("❌ Error marking all notifications as read", e);
        }
    }

    /**
     * Supprimer une notification
     */
    public void deleteNotification(Long notificationId) {
        try {
            notificationApiService.deleteNotification(notificationId);
            log.info("✅ Notification {} deleted", notificationId);
        } catch (Exception e) {
            log.error("❌ Error deleting notification", e);
        }
    }

    /**
     * Supprimer toutes les notifications
     */
    public void clearAllNotifications() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.warn("⚠️ No user logged in");
            return;
        }

        try {
            notificationApiService.clearAllNotifications(userId);
            log.info("✅ All notifications cleared for user {}", userId);
        } catch (Exception e) {
            log.error("❌ Error clearing all notifications", e);
        }
    }

    /**
     * Récupérer les notifications récentes (30 derniers jours)
     */
    public List<NotificationDto> getRecentNotifications() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.warn("⚠️ No user logged in");
            return Collections.emptyList();
        }

        try {
            return notificationApiService.getRecentNotifications(userId);
        } catch (Exception e) {
            log.error("❌ Error fetching recent notifications", e);
            return Collections.emptyList();
        }
    }

    /**
     * Créer une notification pour l'utilisateur connecté
     */
    public CompletableFuture<NotificationDto> createNotification(String message, String type) {
        return CompletableFuture.supplyAsync(() -> {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                log.warn("⚠️ No user logged in, cannot create notification");
                return null;
            }

            try {
                return notificationApiService.createNotification(currentUser, message, type);
            } catch (Exception e) {
                log.error("❌ Error creating notification", e);
                return null;
            }
        });
    }

    /**
     * Notifier qu'un audit est terminé
     */
    public CompletableFuture<NotificationDto> notifyAuditCompleted(AuditResponseDto audit) {
        return CompletableFuture.supplyAsync(() -> {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                log.warn("⚠️ No user logged in");
                return null;
            }

            try {
                return notificationApiService.notifyAuditCompleted(currentUser, audit);
            } catch (Exception e) {
                log.error("❌ Error creating audit completion notification", e);
                return null;
            }
        });
    }

    /**
     * Notifier qu'un audit a échoué
     */
    public CompletableFuture<NotificationDto> notifyAuditFailed(AuditResponseDto audit, String reason) {
        return CompletableFuture.supplyAsync(() -> {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                log.warn("⚠️ No user logged in");
                return null;
            }

            try {
                return notificationApiService.notifyAuditFailed(currentUser, audit, reason);
            } catch (Exception e) {
                log.error("❌ Error creating audit failure notification", e);
                return null;
            }
        });
    }

    /**
     * Notifier qu'un rapport a été généré
     */
    public CompletableFuture<NotificationDto> notifyReportGenerated(AuditResponseDto audit) {
        return CompletableFuture.supplyAsync(() -> {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                log.warn("⚠️ No user logged in");
                return null;
            }

            try {
                return notificationApiService.notifyReportGenerated(currentUser, audit);
            } catch (Exception e) {
                log.error("❌ Error creating report generation notification", e);
                return null;
            }
        });
    }

    /**
     * Vérifier si l'utilisateur a des notifications non lues
     */
    public boolean hasUnreadNotifications() {
        return getUnreadCount() > 0;
    }

    /**
     * Récupérer la dernière notification
     */
    public NotificationDto getLatestNotification() {
        List<NotificationDto> notifications = getAllNotifications();
        if (notifications.isEmpty()) {
            return null;
        }
        return notifications.get(0);
    }

    /**
     * Récupérer le nombre total de notifications
     */
    public int getNotificationsCount() {
        return getAllNotifications().size();
    }

    /**
     * Récupérer l'utilisateur connecté depuis SessionManager
     */
    private User getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }

    /**
     * Récupérer l'ID de l'utilisateur connecté
     */
    private Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}