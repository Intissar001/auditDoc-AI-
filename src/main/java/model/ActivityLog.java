package com.auditdocai.model;

// 6. ActivityLog
// -------------------------------
public class ActivityLog {
private Long id;
private Long userId;
private String action;
private String timestamp;


public ActivityLog() {}


public ActivityLog(Long id, Long userId, String action, String timestamp) {
this.id = id;
this.userId = userId;
this.action = action;
this.timestamp = timestamp;
}


public Long getId() { return id; }
public void setId(Long id) { this.id = id; }


public Long getUserId() { return userId; }
public void setUserId(Long userId) { this.userId = userId; }


public String getAction() { return action; }
public void setAction(String action) { this.action = action; }


public String getTimestamp() { return timestamp; }
public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}