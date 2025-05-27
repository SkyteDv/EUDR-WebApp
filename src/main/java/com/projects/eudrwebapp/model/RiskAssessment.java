package com.projects.eudrwebapp.model;

import com.projects.eudrwebapp.model.Enum.RiskLevel;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.EnumSet;

@Embeddable
public class RiskAssessment {

    public enum RiskFlag {
        MISSING_DDS_ATTACHED(33),
        DDS_DENIED(33),
        HIGH_RISK_PRODUCT_GROUP(20),
        DESTINATION_HABOUR_FULL(20);

        private final int points;

        RiskFlag(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }
    }

    private int score;
    private String actionCode;
    private String hint;

    @Enumerated(EnumType.STRING)
    private RiskLevel level;

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
