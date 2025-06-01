package com.projects.eudrwebapp.model.Enum;

public enum ProductGroup {
    WOOD("WOOD", RiskLevel.LOW),
    COCOA("COCOA", RiskLevel.MEDIUM),
    CATTLE("CATTLE", RiskLevel.HIGH),
    PALM_OIL("PALM OIL", RiskLevel.MEDIUM),
    SOY("SOY", RiskLevel.LOW),
    COFFEE("COFFEE", RiskLevel.MEDIUM),
    RUBBER("RUBBER", RiskLevel.LOW);

    private final String name;
    private final RiskLevel riskLevel;

    ProductGroup(String name, RiskLevel riskLevel) {
        this.name = name;
        this.riskLevel = riskLevel;
    }

    public String getName() {
        return name;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public static ProductGroup fromName(String name) {
        for (ProductGroup pg : values()) {
            if (pg.name.equalsIgnoreCase(name)) {
                return pg;
            }
        }
        throw new IllegalArgumentException("Unknown product group: " + name);
    }
}