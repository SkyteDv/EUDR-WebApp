package com.projects.eudrwebapp.service.riskAltertManagement;

import com.projects.eudrwebapp.model.Enum.OrderStatus;
import com.projects.eudrwebapp.model.Enum.ProductGroup;
import com.projects.eudrwebapp.model.Enum.RiskFlag;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.RiskAssessment;
import com.projects.eudrwebapp.model.Enum.RiskLevel;
import com.projects.eudrwebapp.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

@Service
public class RiskEngine {

    private final OrderRepository orderRepository;


    /**
     * RiskFlags represent specific conditions contributing to the overall risk score of an order.
     * The point values have been carefully assigned to reflect severity:
     * <p>
     * - MISSING_DDS_ATTACHED (40): Critical. Missing documentation is a serious compliance issue.
     * - DDS_DENIED (33): Also critical; suggests rejection of due diligence.
     * - DESTINATION_HARBOUR_FULL (15): Situational operational risk — moderate weight.
     * - HIGH_RISK_PRODUCT_GROUP (15): Indicates potentially sensitive goods — moderate weight.
     * - MEDIUM_RISK_PRODUCT_GROUP (10): Less risky but still worth tracking.
     * - LOW_RISK_PRODUCT_GROUP (0): No risk associated.
     * <p>
     * Thresholds:
     * - ≥ 66 → HIGH risk (e.g., DDS missing + high-risk product + harbour full)
     * - ≥ 33 → MEDIUM risk (e.g., DDS missing + medium-risk product)
     * - < 33 → LOW risk
     * - Unknown values result in UNKNOWN
     */

    public RiskEngine(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void assessOrderRisk(Order order) {
        RiskAssessment new_assessment = new RiskAssessment();
        EnumSet<RiskFlag> activeFlags = EnumSet.noneOf(RiskFlag.class);

        if ((order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.IN_HARBOUR) && !order.isDdsOnDeliveryNote()) {
            activeFlags.add(RiskFlag.MISSING_DDS_ATTACHED);
        }
        if (order.getDdsReferenceNumber().trim().isEmpty()) {
            activeFlags.add(RiskFlag.DDS_DENIED);
        }
        try {
            ProductGroup productGroup = ProductGroup.fromName(order.getProductCategory());

            switch (productGroup.getRiskLevel()) {
                case HIGH:
                    activeFlags.add(RiskFlag.HIGH_RISK_PRODUCT_GROUP);
                    break;
                case MEDIUM:
                    activeFlags.add(RiskFlag.MEDIUM_RISK_PRODUCT_GROUP);
                    break;
                case LOW:
                    activeFlags.add(RiskFlag.LOW_RISK_PRODUCT_GROUP);
                    break;
                default:
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown product category: " + order.getProductCategory());
        }

        //Placeholder Harbour logic. Currently, always applied.
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.CANCELLED) {
            activeFlags.add(RiskFlag.DESTINATION_HARBOUR_FULL);
        }

        int totalScore = activeFlags.stream()
                .mapToInt(RiskFlag::getPoints)
                .sum();

        if (totalScore >= 66) {
            new_assessment.setLevel(RiskLevel.HIGH);
        } else if (totalScore >= 33) {
            new_assessment.setLevel(RiskLevel.MEDIUM);
        } else if (totalScore >= 0) {
            new_assessment.setLevel(RiskLevel.LOW);
        } else {
            new_assessment.setLevel(RiskLevel.UNKNOWN);
        }

        new_assessment.setActionCode(generateCustomActionCode(activeFlags));

        switch (new_assessment.getActionCode()) {
            // No product group
            case "CXA"       -> new_assessment.setHint("Set Valid Product Group.");
            case "CX01A"     -> new_assessment.setHint("Set Valid Product Group. Sent to Storage.");
            case "CX02A"     -> new_assessment.setHint("Set Valid Product Group. Sent to Storage. ");
            case "CX0102A"   -> new_assessment.setHint("Set Valid Product Group. ");
            case "CXF"       -> new_assessment.setHint("Set Valid Product Group. ");
            case "CX01F"     -> new_assessment.setHint("Set Valid Product Group. ");
            case "CX02F"     -> new_assessment.setHint("Set Valid Product Group. ");
            case "CX0102F"   -> new_assessment.setHint("Set Valid Product Group. ");

            // Low risk product group
            case "CLA"       -> new_assessment.setHint("");
            case "CL01A"     -> new_assessment.setHint("");
            case "CL02A"     -> new_assessment.setHint("");
            case "CL0102A"   -> new_assessment.setHint("");
            case "CLF"       -> new_assessment.setHint("");
            case "CL01F"     -> new_assessment.setHint("");
            case "CL02F"     -> new_assessment.setHint("");
            case "CL0102F"   -> new_assessment.setHint("");

            // Medium risk product group
            case "CMA"       -> new_assessment.setHint("");
            case "CM01A"     -> new_assessment.setHint("");
            case "CM02A"     -> new_assessment.setHint("");
            case "CM0102A"   -> new_assessment.setHint("");
            case "CMF"       -> new_assessment.setHint("");
            case "CM01F"     -> new_assessment.setHint("");
            case "CM02F"     -> new_assessment.setHint("");
            case "CM0102F"   -> new_assessment.setHint("");

            // High risk product group
            case "CHA"       -> new_assessment.setHint("");
            case "CH01A"     -> new_assessment.setHint("");
            case "CH02A"     -> new_assessment.setHint("");
            case "CH0102A"   -> new_assessment.setHint("");
            case "CHF"       -> new_assessment.setHint("");
            case "CH01F"     -> new_assessment.setHint("");
            case "CH02F"     -> new_assessment.setHint("");
            case "CH0102F"   -> new_assessment.setHint("");

            default -> throw new IllegalStateException("Unexpected action code: " + new_assessment.getActionCode());
        }


        new_assessment.setScore(totalScore);

        // Replace old assessment with new one in order
        order.setRiskAssessment(new_assessment);
        orderRepository.save(order);
    }

    public String generateCustomActionCode(Set<RiskFlag> flags) {
        StringBuilder sb = new StringBuilder("C");

        // Handle product group letter
        if (flags.contains(RiskFlag.HIGH_RISK_PRODUCT_GROUP)) {
            sb.append("H");
        } else if (flags.contains(RiskFlag.MEDIUM_RISK_PRODUCT_GROUP)) {
            sb.append("M");
        } else if (flags.contains(RiskFlag.LOW_RISK_PRODUCT_GROUP)) {
            sb.append("L");
        } else {
            sb.append("X"); // Default if none set
        }

        // Append all other flag codes (non-product group, non-harbour)
        flags.stream()
                .filter(f -> !f.isProductGroupFlag() && !f.isHarbourFlag())
                .map(RiskFlag::getCode)
                .sorted()
                .forEach(sb::append);

        // Add harbour status at the end
        if (flags.contains(RiskFlag.DESTINATION_HARBOUR_FULL)) {
            sb.append("F");
        } else {
            sb.append("A");
        }

        return sb.toString();
    }
}
