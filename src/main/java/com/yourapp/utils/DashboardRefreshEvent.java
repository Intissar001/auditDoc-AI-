package com.yourapp.utils;

import org.springframework.context.ApplicationEvent;

/**
 * Event déclenché quand le dashboard doit être rafraîchi
 */
public class DashboardRefreshEvent extends ApplicationEvent {

    private final String reason;

    public DashboardRefreshEvent(Object source, String reason) {
        super(source);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}