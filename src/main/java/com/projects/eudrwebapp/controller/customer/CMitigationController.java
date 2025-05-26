package com.projects.eudrwebapp.controller.customer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.OrderStatus;
import com.projects.eudrwebapp.model.RiskLevel;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.AuthService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/mitigation")
public class CMitigationController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;

    public CMitigationController(UserRepository userRepository, OrderRepository orderRepository,
            AuthService authService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.authService = authService;
    }

    @GetMapping
    public String mitigationOverview(
            HttpSession session,
            Model model,
            @SessionAttribute(value = "userId", required = false) String userId) {

        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "redirect:/";
        }

        User customer = userOpt.get();

        List<Order> riskyOrders = orderRepository.findByCustomerAndRiskLevelGreaterThanEqual(customer,
                RiskLevel.MEDIUM);

        // === Fallback bei leerer Liste: Dummy-Daten erzeugen ===
        if (riskyOrders.isEmpty()) {
            Order dummy = new Order();
            dummy.setId(9999L);
            dummy.setProductName("Demo Produkt");
            dummy.setRiskLevel(RiskLevel.HIGH);
            dummy.setErpReferenceNumber("ERP-DEMO-9999");
            dummy.setDestination("Hamburg");
            dummy.setOrderDate(LocalDate.now().minusDays(7));
            dummy.setEstimatedDeliveryDate(LocalDate.now().plusDays(5));
            dummy.setStatus(OrderStatus.PENDING);
            dummy.setDdsReferenceNumber("DDS-TEST-123");
            dummy.setDdsOnDeliveryNote(false);
            dummy.setResponsible_party("demo@example.com");
            dummy.setProductCategory("Wood");
            dummy.setDimensions("100x50x30");

            riskyOrders.add(dummy);
        }
        model.addAttribute("riskyOrders", riskyOrders);
        model.addAttribute("username", customer.getUsername());
        model.addAttribute("HIGH", RiskLevel.HIGH);
        model.addAttribute("MEDIUM", RiskLevel.MEDIUM);

        int highRiskCount = (int) riskyOrders.stream()
                .filter(order -> order.getRiskLevel() == RiskLevel.HIGH)
                .count();

        int mediumRiskCount = (int) riskyOrders.stream()
                .filter(order -> order.getRiskLevel() == RiskLevel.MEDIUM)
                .count();

        model.addAttribute("highRiskCount", highRiskCount);
        model.addAttribute("mediumRiskCount", mediumRiskCount);

        return "c-mitigation";
    }
}
