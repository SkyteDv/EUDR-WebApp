package com.projects.eudrwebapp.service;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.RiskLevel;
import com.projects.eudrwebapp.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RiskManagementService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public RiskManagementService(OrderRepository orderRepository, MailService mailService) {
        this.orderRepository = orderRepository;
        this.mailService = mailService;
    }

    @Scheduled(fixedRate = 10000) // every 10 sec = 10.000.
    @Transactional
    public void checkHighRiskOrders() {
        System.out.println("Checking high risk orders for Mail Warning sent");
        List<RiskLevel> highRiskLevels = List.of(RiskLevel.MEDIUM, RiskLevel.HIGH);
        List<Order> highRiskOrders = orderRepository.findByRiskLevelsAndNotifiedFalseWithAssociations(highRiskLevels);

        for (Order order : highRiskOrders) {
            String subject = "⚠️ High-Risk Order Alert: " + order.getErpReferenceNumber();
            String body = String.format("""
                A high-risk order for Customer %s requires your attention:

                ▸ ERP Reference: %s
                ▸ Product: %s (%s)
                ▸ Dimensions: %s
                ▸ Destination: %s
                ▸ Estimated Delivery: %s

                Please take the necessary action.

                — Your Monitoring System
                """,
                    order.getCustomer().getUsername(),
                    order.getErpReferenceNumber(),
                    order.getProductName(),
                    order.getProductCategory(),
                    order.getDimensions(),
                    order.getDestination(),
                    order.getEstimatedDeliveryDate());

            //mailService.sendNotification(order.getResponsible_party(), subject, body);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Sleep interrupted");
            }
            System.out.println("Sent email to: " + order.getResponsible_party() + " for " + order.getCustomer().getUsername() + "on Order: " + order.getErpReferenceNumber());
            order.setNotified(true);
        }
        orderRepository.saveAll(highRiskOrders); // batch save
    }

    @Scheduled(fixedRate = 10000) //every 10 sec = 10.000.
    @Transactional
    public void updateOrderRiskLevels() {
        System.out.println("Updating Risk Levels for DDS Denied Orders");
        List<Order> allOrders = orderRepository.findAll();
        for (Order order : allOrders) {
            if (order.getDdsReferenceNumber().equalsIgnoreCase("") && order.getRiskLevel() == RiskLevel.LOW) {
                order.setRiskLevel(order.getRiskLevel().increase());
            }
        }
    }
}

