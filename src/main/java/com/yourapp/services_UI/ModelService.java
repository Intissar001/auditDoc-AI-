package com.yourapp.services_UI;

import com.yourapp.dto.AuditTemplateDTO;
import com.yourapp.model.AuditTemplate;
import com.yourapp.DAO.AuditTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelService {

    private final AuditTemplateRepository repository;

    /* =====================================================
       =============== PRIVATE MAPPING =====================
       ===================================================== */

    /**
     * Convertir une entité AuditTemplate en AuditTemplateDTO
     */
    private AuditTemplateDTO mapToDTO(AuditTemplate entity) {
        if (entity == null) return null;

        return new AuditTemplateDTO(
                entity.getId(),
                entity.getName(),
                entity.getOrganization(),
                entity.getDescription(),
                entity.getRuleCount()
        );
    }

    /**
     * Convertir un AuditTemplateDTO en entité AuditTemplate
     */
    private AuditTemplate mapToEntity(AuditTemplateDTO dto) {
        if (dto == null) return null;

        AuditTemplate entity = new AuditTemplate();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setOrganization(dto.getOrganization());
        entity.setDescription(dto.getDescription());
        entity.setRuleCount(dto.getRuleCount());

        return entity;
    }

    /* =====================================================
       ================== PUBLIC API =======================
       ========== (DTO ONLY – NO ENTITY OUT) ================
       ===================================================== */

    /**
     * Récupérer tous les modèles
     */
    public List<AuditTemplateDTO> getAllModels() {
        log.info("📥 Récupération de tous les templates");

        List<AuditTemplateDTO> templates = repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("✅ {} templates récupérés", templates.size());
        return templates;
    }

    /**
     * Récupérer un modèle par ID
     */
    public AuditTemplateDTO getModelById(Long id) {
        log.info("📥 Récupération du template ID={}", id);

        AuditTemplate template = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Template introuvable avec l'id : {}", id);
                    return new RuntimeException("Template introuvable avec l'id : " + id);
                });

        AuditTemplateDTO dto = mapToDTO(template);
        log.info("✅ Template récupéré: {}", dto.getName());
        return dto;
    }

    /**
     * Récupérer les modèles associés à un projet
     * Pour l'instant, retourne tous les templates
     * À modifier plus tard si relation Project-Template existe
     */
    public List<AuditTemplateDTO> getModelsByProject(Long projectId) {
        log.info("📥 Récupération des templates pour projet ID={}", projectId);

        // TODO: Implémenter le filtrage par projet si nécessaire
        List<AuditTemplateDTO> templates = getAllModels();

        log.info("✅ {} templates disponibles pour le projet {}", templates.size(), projectId);
        return templates;
    }

    /**
     * Récupérer les modèles actifs
     * Pour l'instant, retourne tous les templates
     * À modifier si un champ "active" est ajouté
     */
    public List<AuditTemplateDTO> getActiveModels() {
        log.info("📥 Récupération des templates actifs");

        // TODO: Filtrer par status si le champ existe
        return getAllModels();
    }

    /**
     * Rechercher des modèles par nom
     */
    public List<AuditTemplateDTO> searchModels(String query) {
        log.info("🔍 Recherche templates avec mot-clé: '{}'", query);

        if (query == null || query.trim().isEmpty()) {
            log.warn("⚠️ Requête de recherche vide, retour de tous les templates");
            return getAllModels();
        }

        List<AuditTemplateDTO> results = repository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("✅ {} templates trouvés pour '{}'", results.size(), query);
        return results;
    }

    /**
     * Rechercher par organisation
     */
    public List<AuditTemplateDTO> getModelsByOrganization(String organization) {
        log.info("🔍 Recherche templates pour l'organisation: '{}'", organization);

        if (organization == null || organization.trim().isEmpty()) {
            return getAllModels();
        }

        List<AuditTemplateDTO> results = repository.findByOrganization(organization)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.info("✅ {} templates trouvés pour l'organisation '{}'", results.size(), organization);
        return results;
    }

    /**
     * Créer un nouveau template
     */
    public AuditTemplateDTO createModel(AuditTemplateDTO dto) {
        log.info("➕ Création d'un nouveau template: {}", dto.getName());

        AuditTemplate entity = mapToEntity(dto);
        AuditTemplate saved = repository.save(entity);

        AuditTemplateDTO result = mapToDTO(saved);
        log.info("✅ Template créé avec ID={}", result.getId());
        return result;
    }

    /**
     * Mettre à jour un template
     */
    public AuditTemplateDTO updateModel(Long id, AuditTemplateDTO dto) {
        log.info("🔄 Mise à jour du template ID={}", id);

        AuditTemplate existing = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ Template introuvable avec l'id : {}", id);
                    return new RuntimeException("Template introuvable avec l'id : " + id);
                });

        // Mettre à jour les champs
        existing.setName(dto.getName());
        existing.setOrganization(dto.getOrganization());
        existing.setDescription(dto.getDescription());
        existing.setRuleCount(dto.getRuleCount());

        AuditTemplate updated = repository.save(existing);

        AuditTemplateDTO result = mapToDTO(updated);
        log.info("✅ Template mis à jour: {}", result.getName());
        return result;
    }

    /**
     * Supprimer un template
     */
    public void deleteModel(Long id) {
        log.info("🗑️ Suppression du template ID={}", id);

        if (!repository.existsById(id)) {
            log.error("❌ Template introuvable avec l'id : {}", id);
            throw new RuntimeException("Template introuvable avec l'id : " + id);
        }

        repository.deleteById(id);
        log.info("✅ Template supprimé avec succès");
    }

    /**
     * Vérifier si un template existe
     */
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    /**
     * Compter le nombre total de templates
     */
    public long countModels() {
        long count = repository.count();
        log.info("📊 Nombre total de templates: {}", count);
        return count;
    }
}