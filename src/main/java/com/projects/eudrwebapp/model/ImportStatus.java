package com.projects.eudrwebapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_status")
public class ImportStatus {

    @Id
    private String userId; // Primary key

    private boolean completed;

    private LocalDateTime lastUpdated;

    public ImportStatus() {
    }

    public ImportStatus(String userId, boolean completed, LocalDateTime lastUpdated) {
        this.userId = userId;
        this.completed = completed;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
