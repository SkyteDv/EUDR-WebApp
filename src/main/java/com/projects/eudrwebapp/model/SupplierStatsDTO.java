package com.projects.eudrwebapp.model;

public class SupplierStatsDTO {
    private String supplierName;
    private Long supplierId;
    private long totalDeliveries;
    private long greenDeliveries;
    private long redDeliveries;
    private long yellowDeliveries;
    private double greenRate; // Prozentwert

    // --- Konstruktor ---
    public SupplierStatsDTO(String supplierName, Long supplierId, long total, long green, long red, long yellow) {
        this.supplierName = supplierName;
        this.supplierId = supplierId;
        this.totalDeliveries = total;
        this.greenDeliveries = green;
        this.redDeliveries = red;
        this.yellowDeliveries = yellow;
        this.greenRate = total > 0 ? ((double) green / total) * 100 : 0;
    }

    // --- Getter/Setter ---

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public long getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(long totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public long getGreenDeliveries() {
        return greenDeliveries;
    }

    public void setGreenDeliveries(long greenDeliveries) {
        this.greenDeliveries = greenDeliveries;
    }

    public long getRedDeliveries() {
        return redDeliveries;
    }

    public void setRedDeliveries(long redDeliveries) {
        this.redDeliveries = redDeliveries;
    }

    public long getYellowDeliveries() {
        return yellowDeliveries;
    }

    public void setYellowDeliveries(long yellowDeliveries) {
        this.yellowDeliveries = yellowDeliveries;
    }

    public double getGreenRate() {
        return greenRate;
    }

    public void setGreenRate(double greenRate) {
        this.greenRate = greenRate;
    }
}
