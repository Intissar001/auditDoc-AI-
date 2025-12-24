package com.yourapp.services;

import com.yourapp.DAO.AuditDocumentRepository;
import com.yourapp.model.AuditDocument;
import com.yourapp.model.Project;
import com.yourapp.DAO.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired private AuditDocumentRepository auditDocumentRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public void saveProject(Project project) {
        projectRepository.save(project);
    }

    public List<AuditDocument> getDocumentsByProjectId(Long projectId) {
        return auditDocumentRepository.findByProjectId(projectId);
    }

    public void deleteDocument(AuditDocument doc) {
        auditDocumentRepository.delete(doc);
    }
}