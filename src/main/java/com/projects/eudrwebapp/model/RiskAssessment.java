package com.projects.eudrwebapp.model;

import com.projects.eudrwebapp.model.Enum.RiskFlag;
import com.projects.eudrwebapp.model.Enum.RiskLevel;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.EnumSet;
import java.util.Set;

@Embeddable
public class RiskAssessment {

    private int score;
    private String actionCode;
    private int actionGroup;
    private String hint;

    @Enumerated(EnumType.STRING)
    private RiskLevel level;

    private Set<RiskFlag> flags = EnumSet.noneOf(RiskFlag.class);

    // Constructor
    public RiskAssessment() {
        this.score = 0;
        this.actionCode = "NONE";
        this.hint = "No assessment available";
        this.level = RiskLevel.UNKNOWN;
    }

    public RiskAssessment(int score, String actionCode, String hint, RiskLevel level) {
        this.score = score;
        this.actionCode = actionCode;
        this.hint = hint;
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

    public String getHint() {
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

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setLevel(RiskLevel level) {
        this.level = level;
    }
}
