package com.yourapp.model;

// 5. UserSession
// -------------------------------
public class UserSession {
private Long id;
private Long userId;
private String token;
private String createdAt;


public UserSession() {}


public UserSession(Long id, Long userId, String token, String createdAt) {
this.id = id;
this.userId = userId;
this.token = token;
this.createdAt = createdAt;
}


public Long getId() { return id; }
public void setId(Long id) { this.id = id; }


public Long getUserId() { return userId; }
public void setUserId(Long userId) { this.userId = userId; }


public String getToken() { return token; }
public void setToken(String token) { this.token = token; }


public String getCreatedAt() { return createdAt; }
public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}