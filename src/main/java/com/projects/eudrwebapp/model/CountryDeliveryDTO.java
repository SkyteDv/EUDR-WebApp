package com.projects.eudrwebapp.model;

public class CountryDeliveryDTO {
    private String country;
    private int deliveries;

    // Constructor
    public CountryDeliveryDTO(String country, int deliveries) {
        this.country = country;
        this.deliveries = deliveries;
    }

    // Getters and setters
    public String getCountry() {
        return country;
    }

    public int getDeliveries() {
        return deliveries;
    }
}

