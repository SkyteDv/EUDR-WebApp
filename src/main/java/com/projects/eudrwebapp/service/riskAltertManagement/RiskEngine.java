package com.projects.eudrwebapp.service.riskAltertManagement;

import com.projects.eudrwebapp.model.Enum.OrderStatus;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.RiskAssessment;
import com.projects.eudrwebapp.model.Enum.RiskLevel;
import com.projects.eudrwebapp.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class RiskEngine {

    private final OrderRepository orderRepository;

    public enum RiskFlag {
        MISSING_DDS_ATTACHED(33),
        DDS_DENIED(33),
        HIGH_RISK_PRODUCT_GROUP(20),
        DESTINATION_HABOUR_FULL(20);

        private final int points;

        RiskFlag(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }
    }

    public RiskEngine(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void assessOrderRisk(Order order) {
        RiskAssessment new_assessment = new RiskAssessment();
        EnumSet<RiskFlag> activeFlags = EnumSet.noneOf(RiskFlag.class);

        // TODO: Add logic here to detect flags from order data, e.g.
        if ((order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.IN_HARBOUR) && !order.isDdsOnDeliveryNote()) {
            activeFlags.add(RiskFlag.MISSING_DDS_ATTACHED);
        }
        if (order.getDdsReferenceNumber().trim().isEmpty()) {
            activeFlags.add(RiskFlag.DDS_DENIED);
        }
        //     activeFlags.add(RiskFlag.MISSING_DDS_ATTACHED);
        // if (order DDS is denied)
        //     activeFlags.add(RiskFlag.DDS_DENIED);
        // if (order product group is high risk)
        //     activeFlags.add(RiskFlag.HIGH_RISK_PRODUCT_GROUP);
        // if (destination harbour is full)
        //     activeFlags.add(RiskFlag.DESTINATION_HABOUR_FULL);

        int totalScore = activeFlags.stream()
                .mapToInt(RiskFlag::getPoints)
                .sum();

        System.out.println(order.getId());
        System.out.println(totalScore);
        activeFlags.forEach(System.out::println);

        if (totalScore >= 66) {
            new_assessment.setLevel(RiskLevel.HIGH);
        } else if (totalScore >= 33) {
            new_assessment.setLevel(RiskLevel.MEDIUM);
        } else if (totalScore >= 0) {
            new_assessment.setLevel(RiskLevel.LOW);
        } else {
            new_assessment.setLevel(RiskLevel.UNKNOWN);
        }

        new_assessment.setScore(totalScore);

        // Replace old assessment with new one in order
        order.setRiskAssessment(new_assessment);
        orderRepository.save(order);
    }
}
