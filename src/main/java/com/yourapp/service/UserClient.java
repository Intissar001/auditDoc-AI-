package com.yourapp.service;

import com.yourapp.model.User;

public class UserClient {

    public static User getCurrentUser() {
        try {
            return ApiClient.get("/users/current", User.class);
        } catch (Exception e) {
            System.err.println("❌ Cannot load current user: " + e.getMessage());
            return null;
        }
    }
}
