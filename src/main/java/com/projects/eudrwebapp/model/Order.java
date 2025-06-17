package com.projects.eudrwebapp.model;

import com.projects.eudrwebapp.model.Enum.OrderStatus;
import com.projects.eudrwebapp.model.Enum.RiskLevel;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "harbour_id")
    private Harbour destination;
    private LocalDate orderDate;
    private LocalDate estimatedDeliveryDate;
    private String ddsReferenceNumber;
    private String responsible_party;

    @Embedded
    private RiskAssessment riskAssessment;

    private boolean notified = false;
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

    public Order(String erpReferenceNumber,String productCategory, String productName, String dimensions, Harbour destination,
                 LocalDate orderDate, LocalDate estimatedDeliveryDate, String ddsReferenceNumber,
                 boolean ddsOnDeliveryNote, OrderStatus status,
                 User supplier, User customer, String responsible_party, RiskAssessment riskAssessment) {
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
        this.notified = false;
        this.riskAssessment = riskAssessment;
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

    public Harbour getDestination() {
        return destination;
    }

    public void setDestination(Harbour destination) {
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

    public RiskLevel getRiskLevel() {
        return riskAssessment.getLevel();
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskAssessment.setLevel(riskLevel);
    }

    public RiskAssessment getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(RiskAssessment riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
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
