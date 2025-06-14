package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.Enum.OrderStatus;
import com.projects.eudrwebapp.model.Enum.RiskLevel;
import com.projects.eudrwebapp.model.Harbour;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.HarbourRepository;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("customer/harbour/")
public class CHarbourController {
    private final OrderRepository orderRepository;
    HarbourRepository harbourRepository;
    AuthService authService;

    public CHarbourController(HarbourRepository harbourRepository, AuthService authService, OrderRepository orderRepository) {
        this.harbourRepository = harbourRepository;
        this.authService = authService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("overview")
    public String harbour(HttpSession session) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }
        return "c-manage-harbours";
    }

    @GetMapping("details/{harbourName}")
    public String details(HttpSession session, @PathVariable String harbourName, Model model) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        Optional<Harbour> optHarbour = harbourRepository.findByName(harbourName);
        if (optHarbour.isEmpty()) {
            return "redirect:/customer/harbour/overview";
        }

        Harbour harbour = optHarbour.get();
        List<Order> harbourSpecificOrders = orderRepository.findByDestination(harbour);

        long activeOrdersCount = harbourSpecificOrders.stream()
                .filter(order -> order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.CANCELLED)
                .count();

        long activeMediumHighRiskOrdersCount = harbourSpecificOrders.stream()
                .filter(order -> order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.CANCELLED)
                .filter(order -> {
                    RiskLevel level = order.getRiskAssessment().getLevel();
                    return level == RiskLevel.MEDIUM || level == RiskLevel.HIGH;
                })
                .count();

        long ordersInStorageCount = harbourSpecificOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.IN_STORAGE)
                .count();

        model.addAttribute("harbour", harbour);
        model.addAttribute("harbourSpecificOrders", harbourSpecificOrders);
        model.addAttribute("activeOrdersCount", activeOrdersCount);
        model.addAttribute("activeMediumHighRiskOrdersCount", activeMediumHighRiskOrdersCount);
        model.addAttribute("ordersInStorageCount", ordersInStorageCount);

        return "c-harbour-details";
    }


}
