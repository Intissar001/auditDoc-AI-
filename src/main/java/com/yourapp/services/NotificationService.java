package com.yourapp.services;

import com.yourapp.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private static List<Notification> notifications = new ArrayList<>();

    public NotificationService() {
        if (notifications.isEmpty()) {
            notifications.add(new Notification("Nouveau rapport généré"));
            notifications.add(new Notification("Un projet a été mis à jour"));
        }
    }

    public int countUnread() {
        return (int) notifications.stream().filter(n -> !n.isRead()).count();
    }

    public List<Notification> getAll() {
        return notifications;
    }

    public void addNotification(String message) {
        notifications.add(new Notification(message));
    }
}
