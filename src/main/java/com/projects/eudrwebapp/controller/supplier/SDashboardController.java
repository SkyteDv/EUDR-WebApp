package com.projects.eudrwebapp.controller.supplier;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.model.Enum.OrderStatus;
import com.projects.eudrwebapp.model.Enum.RiskLevel;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/supplier/dashboard")
public class SDashboardController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;

    public SDashboardController(UserRepository userRepository, AuthService authService, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public String home(HttpSession session, Model model) {
        if (!authService.validateUserAuth(session, "SUPPLIER")) {
            return "redirect:/";
        }
        
        Long userId = (Long) session.getAttribute("userId");
        Optional<User> userOpt = userRepository.findById(userId.toString());
        
        if (userOpt.isEmpty()) {
            return "redirect:/";
        }
        
        User supplier = userOpt.get();
        
        // Get all orders for this supplier
        List<Order> allOrders = orderRepository.findBySupplierId(userId);
        
        // Calculate statistics
        int totalCustomers = (int) allOrders.stream()
                .map(order -> order.getCustomer().getId())
                .distinct()
                .count();
        
        int totalDeliveries = allOrders.size();
        
        int activeDeliveries = (int) allOrders.stream()
                .filter(order -> !order.getStatus().equals(OrderStatus.SHIPPED))
                .count();
        
        int completedDeliveries = (int) allOrders.stream()
                .filter(order -> order.getStatus().equals(OrderStatus.SHIPPED))
                .count();
        
        int highRiskDeliveries = (int) allOrders.stream()
                .filter(order -> order.getRiskLevel() == RiskLevel.HIGH)
                .count();
        
        int pendingDDS = (int) allOrders.stream()
                .filter(order -> "Not Available".equals(order.getDdsStatus()))
                .count();
        
        int approvedDDS = (int) allOrders.stream()
                .filter(order -> "Yes".equals(order.getDdsStatus()))
                .count();
        
        // Calculate DDS approval rate
        double ddsApprovalRate = totalDeliveries > 0 ? (double) approvedDDS / totalDeliveries * 100 : 0.0;
        
        // Get recent deliveries (last 10)
        List<Order> recentDeliveries = allOrders.stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .limit(10)
                .collect(Collectors.toList());
        
        // Risk level distribution
        Map<RiskLevel, Long> riskDistribution = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getRiskLevel, Collectors.counting()));
        
        // Add attributes to model
        model.addAttribute("supplier", supplier);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalDeliveries", totalDeliveries);
        model.addAttribute("activeDeliveries", activeDeliveries);
        model.addAttribute("completedDeliveries", completedDeliveries);
        model.addAttribute("highRiskDeliveries", highRiskDeliveries);
        model.addAttribute("pendingDDS", pendingDDS);
        model.addAttribute("approvedDDS", approvedDDS);
        model.addAttribute("ddsApprovalRate", Math.round(ddsApprovalRate * 10.0) / 10.0);
        model.addAttribute("recentDeliveries", recentDeliveries);
        model.addAttribute("riskDistribution", riskDistribution);
        
        return "s-dashboard";
    }
}
