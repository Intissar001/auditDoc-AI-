package com.yourapp.service;

import com.google.gson.reflect.TypeToken;
import com.yourapp.dto.AuditTemplateDTO;
import com.yourapp.dto.UserSettingsDTO;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class SettingsClient {

    // ===== Templates =====

    public static List<AuditTemplateDTO> getAllTemplates() {
        try {
            Type type = new TypeToken<List<AuditTemplateDTO>>(){}.getType();
            return ApiClient.getList("/settings/templates", type);
        } catch (Exception e) {
            System.err.println("❌ Error loading templates: " + e.getMessage());
            return List.of();
        }
    }

    public static boolean createTemplate(String name, String org, String desc) {
        try {
            Map<String, String> body = Map.of(
                    "name", name,
                    "organization", org,
                    "description", desc
            );
            ApiClient.post("/settings/templates", body, AuditTemplateDTO.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteTemplate(Long id) {
        try {
            return ApiClient.delete("/settings/templates/" + id);
        } catch (Exception e) {
            return false;
        }
    }

    // ===== User Settings =====

    public static UserSettingsDTO getUserSettings(Long userId) {
        try {
            return ApiClient.get("/settings/user/" + userId, UserSettingsDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean updateUserSettings(Long userId, boolean email, boolean audit) {
        try {
            UserSettingsDTO dto = new UserSettingsDTO(userId, null, email, audit);
            ApiClient.put("/settings/user/" + userId, dto, UserSettingsDTO.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
