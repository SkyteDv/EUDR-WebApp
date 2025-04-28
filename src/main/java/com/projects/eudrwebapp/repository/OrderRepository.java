package com.projects.eudrwebapp.repository;

import com.projects.eudrwebapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findBySupplierId(Long supplierId);

    Optional<Order> findByDdsReferenceNumber(String ddsReferenceNumber);

    Optional<Order> findByErpReferenceNumber(String erpReferenceNumber);

}
