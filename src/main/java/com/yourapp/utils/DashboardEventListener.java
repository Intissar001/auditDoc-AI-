package com.yourapp.utils;

import com.yourapp.utils.DashboardRefreshEvent;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.ref.WeakReference;

/**
 * Listener pour gérer les événements de rafraîchissement du dashboard
 */
@Component
@Slf4j
public class DashboardEventListener {

    // Utilisation de WeakReference pour éviter les fuites mémoire
    private WeakReference<com.yourapp.controller.DashboardController> dashboardControllerRef;

    /**
     * Enregistrer le contrôleur du dashboard
     */
    public void registerDashboardController(com.yourapp.controller.DashboardController controller) {
        this.dashboardControllerRef = new WeakReference<>(controller);
        log.info("✅ DashboardController enregistré pour les événements");
    }

    /**
     * Écouter les événements de rafraîchissement
     */
    @EventListener
    public void handleDashboardRefresh(DashboardRefreshEvent event) {
        log.info("🔔 Événement reçu: {}", event.getReason());

        if (dashboardControllerRef != null) {
            com.yourapp.controller.DashboardController controller = dashboardControllerRef.get();

            if (controller != null) {
                // Rafraîchir le dashboard sur le thread JavaFX
                Platform.runLater(() -> {
                    try {
                        controller.refresh();
                        log.info("✅ Dashboard rafraîchi suite à: {}", event.getReason());
                    } catch (Exception e) {
                        log.error("❌ Erreur lors du rafraîchissement du dashboard", e);
                    }
                });
            } else {
                log.warn("⚠️ DashboardController n'est plus disponible");
            }
        }
    }
}