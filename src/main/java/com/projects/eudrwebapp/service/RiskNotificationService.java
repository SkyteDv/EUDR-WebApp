package com.projects.eudrwebapp.service;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.RiskLevel;
import com.projects.eudrwebapp.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RiskNotificationService {

    private final OrderRepository orderRepository;
    private final MailService mailService;

    public RiskNotificationService(OrderRepository orderRepository, MailService mailService) {
        this.orderRepository = orderRepository;
        this.mailService = mailService;
    }

    @Scheduled(fixedRate = 10000) // every 10 sec = 10.000.
    @Transactional
    public void checkHighRiskOrders() {
        System.out.println("Checking high risk orders");
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

            mailService.sendNotification(order.getResponsible_party(), subject, body);
            System.out.println("Sent email to: " + order.getResponsible_party() + " for " + order.getCustomer().getUsername() + "on Order: " + order.getErpReferenceNumber());
            order.setNotified(true);
        }
        orderRepository.saveAll(highRiskOrders); // batch save
    }
}

