package com.projects.eudrwebapp.model.Enum;

public enum RiskFlag {
    MISSING_DDS_ATTACHED(40, "01"),
    DDS_DENIED(33, "02"),
    DESTINATION_HARBOUR_FULL(15, "03"),
    HIGH_RISK_PRODUCT_GROUP(15, "04"),
    MEDIUM_RISK_PRODUCT_GROUP(10, "05"),
    LOW_RISK_PRODUCT_GROUP(0, "06");

    private final int points;
    private final String code;

    RiskFlag(int points, String code) {
        this.points = points;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public int getPoints() {
        return points;
    }

    public boolean isProductGroupFlag() {
        return this == HIGH_RISK_PRODUCT_GROUP ||
                this == MEDIUM_RISK_PRODUCT_GROUP ||
                this == LOW_RISK_PRODUCT_GROUP;
    }

    public boolean isHarbourFlag() {
        return this == DESTINATION_HARBOUR_FULL;
    }
}
