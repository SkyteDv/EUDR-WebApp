package com.projects.eudrwebapp.model;

import com.projects.eudrwebapp.model.Enum.RiskFlag;
import com.projects.eudrwebapp.model.Enum.RiskLevel;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Embeddable
public class RiskAssessment {

    private int score;
    private String actionCode;
    private int actionGroup;

    @ElementCollection
    @MapKeyColumn(name = "hint_key")
    @Column(name = "hint_value")
    @CollectionTable(name = "risk_assessment_hints", joinColumns = @JoinColumn(name = "assessment_id"))
    private Map<String, String> hint;

    @Enumerated(EnumType.STRING)
    private RiskLevel level;

    private Set<RiskFlag> flags = EnumSet.noneOf(RiskFlag.class);

    // Constructor
    public RiskAssessment() {
        this.score = 0;
        this.actionCode = "NONE";
        this.hint = new HashMap<>();
        this.level = RiskLevel.UNKNOWN;
    }

    public RiskAssessment(int score, String actionCode, RiskLevel level) {
        this.score = score;
        this.actionCode = actionCode;
        this.hint = new HashMap<>();
        this.level = level;
    }

    // New getter/setter
    public Set<RiskFlag> getFlags() {
        return flags;
    }

    public void setFlags(Set<RiskFlag> flags) {
        this.flags = EnumSet.copyOf(flags);
    }

    // Existing getters/setters
    public int getScore() {
        return score;
    }

    public String getActionCode() {
        return actionCode;
    }

    public Map<String, String> getHint() {
        return hint;
    }

    public RiskLevel getLevel() {
        return level;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public void setHint(Map<String, String> hint) {
        this.hint = hint;
    }

    public void addItemToHint(String key, String value) {
        hint.put(key, value);
    }

    public void setLevel(RiskLevel level) {
        this.level = level;
    }
}
