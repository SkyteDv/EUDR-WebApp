package com.projects.eudrwebapp.model.DTO;

import java.time.LocalDate;

public class SupplierCustomerDTO {
    private Long customerId;
    private String customerName;
    private int totalSharedDeliveries;
    private LocalDate lastDeliveryDate;
    private int ddsYes;
    private int ddsNo;
    private int ddsDenied;
    private String country;
    private LocalDate firstDeliveryDate;

    public SupplierCustomerDTO(Long customerId, String customerName, int totalSharedDeliveries,
                                LocalDate lastDeliveryDate, int ddsYes, int ddsNo, int ddsDenied,
                                String country, LocalDate firstDeliveryDate) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalSharedDeliveries = totalSharedDeliveries;
        this.lastDeliveryDate = lastDeliveryDate;
        this.ddsYes = ddsYes;
        this.ddsNo = ddsNo;
        this.ddsDenied = ddsDenied;
        this.country = country;
        this.firstDeliveryDate = firstDeliveryDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getTotalSharedDeliveries() {
        return totalSharedDeliveries;
    }

    public void setTotalSharedDeliveries(int totalSharedDeliveries) {
        this.totalSharedDeliveries = totalSharedDeliveries;
    }

    public LocalDate getLastDeliveryDate() {
        return lastDeliveryDate;
    }

    public void setLastDeliveryDate(LocalDate lastDeliveryDate) {
        this.lastDeliveryDate = lastDeliveryDate;
    }

    public int getDdsYes() {
        return ddsYes;
    }

    public void setDdsYes(int ddsYes) {
        this.ddsYes = ddsYes;
    }

    public int getDdsNo() {
        return ddsNo;
    }

    public void setDdsNo(int ddsNo) {
        this.ddsNo = ddsNo;
    }

    public int getDdsDenied() {
        return ddsDenied;
    }

    public void setDdsDenied(int ddsDenied) {
        this.ddsDenied = ddsDenied;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getFirstDeliveryDate() {
        return firstDeliveryDate;
    }

    public void setFirstDeliveryDate(LocalDate firstDeliveryDate) {
        this.firstDeliveryDate = firstDeliveryDate;
    }

    
}
