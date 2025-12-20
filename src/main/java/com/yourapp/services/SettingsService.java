package com.yourapp.services;

import com.yourapp.DAO.AuditTemplateRepository;
import com.yourapp.DAO.UserRepository;
import com.yourapp.dto.AuditTemplateDTO;
import com.yourapp.dto.CreateTemplateRequest;
import com.yourapp.dto.UserSettingsDTO;
import com.yourapp.model.AuditTemplate;
import com.yourapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SettingsService {

    @Autowired
    private AuditTemplateRepository templateRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== TEMPLATES ====================

    public List<AuditTemplateDTO> getAllTemplates() {
        return templateRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AuditTemplateDTO createTemplate(CreateTemplateRequest request) {
        AuditTemplate template = new AuditTemplate(
                request.getName(),
                request.getOrganization(),
                request.getDescription(),
                0
        );
        AuditTemplate saved = templateRepository.save(template);
        return convertToDTO(saved);
    }

    @Transactional
    public boolean deleteTemplate(Long id) {
        if (templateRepository.existsById(id)) {
            templateRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public AuditTemplateDTO updateTemplate(Long id, CreateTemplateRequest request) {
        return templateRepository.findById(id)
                .map(template -> {
                    template.setName(request.getName());
                    template.setOrganization(request.getOrganization());
                    template.setDescription(request.getDescription());
                    return convertToDTO(templateRepository.save(template));
                })
                .orElse(null);
    }

    // ==================== USER SETTINGS ====================

    public UserSettingsDTO getUserSettings(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new UserSettingsDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getEmailAlerts(),
                        user.getAuditReminders()
                ))
                .orElse(null);
    }

    @Transactional
    public UserSettingsDTO updateUserSettings(Long userId, Boolean emailAlerts, Boolean auditReminders) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setEmailAlerts(emailAlerts);
                    user.setAuditReminders(auditReminders);
                    User updated = userRepository.save(user);
                    return new UserSettingsDTO(
                            updated.getId(),
                            updated.getEmail(),
                            updated.getEmailAlerts(),
                            updated.getAuditReminders()
                    );
                })
                .orElse(null);
    }

    // ==================== HELPERS ====================

    private AuditTemplateDTO convertToDTO(AuditTemplate template) {
        return new AuditTemplateDTO(
                template.getId(),
                template.getName(),
                template.getOrganization(),
                template.getDescription(),
                template.getRuleCount()
        );
    }
}