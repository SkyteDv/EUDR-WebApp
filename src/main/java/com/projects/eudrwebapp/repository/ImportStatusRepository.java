package com.projects.eudrwebapp.repository;

import com.projects.eudrwebapp.model.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportStatusRepository extends JpaRepository<ImportStatus, String> {
}

