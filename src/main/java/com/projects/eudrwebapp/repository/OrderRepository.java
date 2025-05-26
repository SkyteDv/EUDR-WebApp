package com.projects.eudrwebapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.OrderStatus;
import com.projects.eudrwebapp.model.RiskLevel;
import com.projects.eudrwebapp.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findBySupplierId(Long supplier_id);

    List<Order> findByCustomerAndSupplier(User customer, User supplier);

    Optional<Order> findByDdsReferenceNumber(String ddsReferenceNumber);

    Optional<Order> findByErpReferenceNumber(String erpReferenceNumber);

    List<Order> findByStatus(OrderStatus orderStatus);

    List<Order> findByRiskLevelAndNotifiedFalse(RiskLevel riskLevel);

    List<Order> findByCustomerAndRiskLevelGreaterThanEqual(User customer, RiskLevel riskLevel);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.riskLevel IN :levels AND o.notified = false")
    List<Order> findByRiskLevelsAndNotifiedFalseWithAssociations(@Param("levels") List<RiskLevel> levels);

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.ddsOnDeliveryNote = true WHERE o.id = :orderId")
    int markDDSAsAttached(Long orderId);

}
