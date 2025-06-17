package com.projects.eudrwebapp.repository;

import com.projects.eudrwebapp.model.Harbour;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.Enum.OrderStatus;
import com.projects.eudrwebapp.model.Enum.RiskLevel;
import com.projects.eudrwebapp.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.Destination;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findById(Long orderId);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findBySupplierId(Long supplier_id);

    List<Order> findByCustomerAndSupplier(User customer, User supplier);

    List<Order> findByDestination(Harbour destination);

    Optional<Order> findByDdsReferenceNumber(String ddsReferenceNumber);

    Optional<Order> findByErpReferenceNumber(String erpReferenceNumber);

    List<Order> findByStatus(OrderStatus orderStatus);

    List<Order> findByRiskAssessment_LevelAndNotifiedFalse(RiskLevel level);

    @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.riskAssessment.level IN :levels AND o.notified = false")
    List<Order> findByRiskLevelsAndNotifiedFalseWithAssociations(@Param("levels") List<RiskLevel> levels);

    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.ddsOnDeliveryNote = true WHERE o.id = :orderId")
    int markDDSAsAttached(Long orderId);

}
