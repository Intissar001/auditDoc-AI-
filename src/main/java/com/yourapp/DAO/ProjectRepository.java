package com.yourapp.DAO;

import com.yourapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // C'est tout ! Spring génère le code SQL pour findAll, save, delete... tout seul.
}