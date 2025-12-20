package com.yourapp.API;

import com.yourapp.dto.AuditTemplateDTO;
import com.yourapp.dto.CreateTemplateRequest;
import com.yourapp.dto.UserSettingsDTO;
import com.yourapp.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "*")
public class SettingsRestController {

    @Autowired
    private SettingsService settingsService;

    // ==================== Audit Templates ====================

    @GetMapping("/templates")
    public ResponseEntity<List<AuditTemplateDTO>> getAllTemplates() {
        List<AuditTemplateDTO> templates = settingsService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }

    @PostMapping("/templates")
    public ResponseEntity<AuditTemplateDTO> createTemplate(@RequestBody CreateTemplateRequest request) {
        AuditTemplateDTO created = settingsService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/templates/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        boolean deleted = settingsService.deleteTemplate(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/templates/{id}")
    public ResponseEntity<AuditTemplateDTO> updateTemplate(
            @PathVariable Long id,
            @RequestBody CreateTemplateRequest request) {
        AuditTemplateDTO updated = settingsService.updateTemplate(id, request);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== User Settings ====================

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserSettingsDTO> getUserSettings(@PathVariable Long userId) {
        UserSettingsDTO settings = settingsService.getUserSettings(userId);
        if (settings != null) {
            return ResponseEntity.ok(settings);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserSettingsDTO> updateUserSettings(
            @PathVariable Long userId,
            @RequestBody UserSettingsDTO request) {
        // Extraire les champs du DTO
        UserSettingsDTO updated = settingsService.updateUserSettings(
                userId,
                request.getEmailAlerts(),
                request.getAuditReminders()
        );
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
}