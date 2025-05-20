package com.projects.eudrwebapp.model;

public enum RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN;

    public static RiskLevel fromString(String s) {
        if (s == null) return UNKNOWN;
        try {
            return RiskLevel.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public RiskLevel increase() {
        return switch (this) {
            case LOW -> MEDIUM;
            case MEDIUM -> HIGH;
            default -> this;
        };
    }

    public RiskLevel decrease() {
        return switch (this) {
            case HIGH -> MEDIUM;
            case MEDIUM -> LOW;
            default -> this;
        };
    }
}
