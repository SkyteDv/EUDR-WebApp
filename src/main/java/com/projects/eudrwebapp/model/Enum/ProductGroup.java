package com.projects.eudrwebapp.model.Enum;

public enum ProductGroup {
    WOOD("WOOD", RiskLevel.LOW),
    BEEF("BEEF", RiskLevel.HIGH),
    CHEMICALS("CHEMICALS", RiskLevel.HIGH),
    ELECTRONICS("ELECTRONICS", RiskLevel.MEDIUM),
    TEXTILES("TEXTILES", RiskLevel.LOW);

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

