package com.projects.eudrwebapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String erpReferenceNumber;
    private String productCategory;
    private String productName;
    private String dimensions;
    private String destination;
    private LocalDate orderDate;
    private LocalDate estimatedDeliveryDate;
    private String ddsReferenceNumber;
    private String responsible_party;

    private boolean ddsOnDeliveryNote;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private User supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    // --- Constructors ---

    public Order() {
    }

    public Order(String erpReferenceNumber,String productCategory, String productName, String dimensions, String destination,
                 LocalDate orderDate, LocalDate estimatedDeliveryDate, String ddsReferenceNumber,
                 boolean ddsOnDeliveryNote, OrderStatus status,
                 User supplier, User customer, String responsible_party) {
        this.erpReferenceNumber = erpReferenceNumber;
        this.productCategory = productCategory;
        this.productName = productName;
        this.dimensions = dimensions;
        this.destination = destination;
        this.orderDate = orderDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.ddsReferenceNumber = ddsReferenceNumber;
        this.ddsOnDeliveryNote = ddsOnDeliveryNote;
        this.status = status;
        this.supplier = supplier;
        this.customer = customer;
        this.responsible_party = responsible_party;
    }

    public String getDdsStatus() {
        if (ddsReferenceNumber == null || ddsReferenceNumber.isEmpty()) {
            return "Not Available";  // Red
        } else if (ddsOnDeliveryNote) {
            return "Yes";  // Green
        } else {
            return "No";  // Yellow
        }
    }

    // Method to calculate the risk level
    public String calculateRiskLevel() {
        String ddsStatus = getDdsStatus();

        if ("Yes".equalsIgnoreCase(ddsStatus)) {
            return switch (status) {
                case PENDING, SHIPPED, IN_HARBOUR, PASSED_CUSTOMS -> "Low";
                default -> "Unknown";
            };
        } else if ("No".equalsIgnoreCase(ddsStatus)) {
            return switch (status) {
                case PENDING -> "Low";
                case SHIPPED, IN_HARBOUR -> "Medium";
                case PASSED_CUSTOMS -> "Very High";
                default -> "Unknown";
            };
        } else if ("Not Available".equalsIgnoreCase(ddsStatus)) {
            return switch (status) {
                case PENDING -> "Medium";
                case SHIPPED, IN_HARBOUR -> "High";
                case PASSED_CUSTOMS -> "Very High";
                default -> "Unknown";
            };
        } else {
            return "Unknown";
        }
    }


    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErpReferenceNumber() {
        return erpReferenceNumber;
    }

    public void setErpReferenceNumber(String erpReferenceNumber) {
        this.erpReferenceNumber = erpReferenceNumber;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getDdsReferenceNumber() {
        return ddsReferenceNumber;
    }

    public void setDdsReferenceNumber(String ddsReferenceNumber) {
        this.ddsReferenceNumber = ddsReferenceNumber;
    }

    public boolean isDdsOnDeliveryNote() {
        return ddsOnDeliveryNote;
    }

    public void setDdsOnDeliveryNote(boolean ddsOnDeliveryNote) {
        this.ddsOnDeliveryNote = ddsOnDeliveryNote;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public User getSupplier() {
        return supplier;
    }

    public void setSupplier(User supplier) {
        this.supplier = supplier;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public String getResponsible_party() {
        return responsible_party;
    }

    public void setResponsible_party(String responsible_party) {
        this.responsible_party = responsible_party;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", erpReferenceNumber='" + erpReferenceNumber + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", productName='" + productName + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", destination='" + destination + '\'' +
                ", orderDate=" + orderDate +
                ", estimatedDeliveryDate=" + estimatedDeliveryDate +
                ", ddsReferenceNumber='" + ddsReferenceNumber + '\'' +
                ", ddsOnDeliveryNote=" + ddsOnDeliveryNote +
                ", status=" + status +
                ", supplier=" + supplier +
                ", customer=" + customer +
                "; responsible_party='" + responsible_party + '\'' +
                '}';
    }
}
