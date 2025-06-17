package com.projects.eudrwebapp.controller.customer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projects.eudrwebapp.model.Harbour;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.HarbourRepository;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import com.projects.eudrwebapp.service.riskAltertManagement.RiskEngine;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("customer/risk")
public class CRiskController {

    AuthService authService;
    UserRepository userRepository;
    OrderRepository orderRepository;
    HarbourRepository harbourRepository;
    RiskEngine riskEngine;

    public CRiskController(AuthService authService, UserRepository userRepository, OrderRepository orderRepository,
            HarbourRepository harbourRepository, RiskEngine riskEngine) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.harbourRepository = harbourRepository;
        this.riskEngine = riskEngine;
    }

    @GetMapping("/overview")
    public String overview(HttpSession session, Model model, @SessionAttribute(value = "userId", required = false) Long userId) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        // Fetch customer's orders for risk analysis
        List<Order> customerOrders = orderRepository.findByCustomerId(userId);
        
        // Calculate risk statistics
        long totalOrders = customerOrders.size();
        long highRiskOrders = customerOrders.stream()
                .filter(order -> order.getRiskLevel() == com.projects.eudrwebapp.model.Enum.RiskLevel.HIGH)
                .count();
        long mediumRiskOrders = customerOrders.stream()
                .filter(order -> order.getRiskLevel() == com.projects.eudrwebapp.model.Enum.RiskLevel.MEDIUM)
                .count();
        long lowRiskOrders = customerOrders.stream()
                .filter(order -> order.getRiskLevel() == com.projects.eudrwebapp.model.Enum.RiskLevel.LOW)
                .count();
        long unknownRiskOrders = customerOrders.stream()
                .filter(order -> order.getRiskLevel() == com.projects.eudrwebapp.model.Enum.RiskLevel.UNKNOWN)
                .count();
        
        // Filter active orders (non-completed, non-cancelled)
        List<Order> activeOrders = customerOrders.stream()
                .filter(order -> order.getStatus() != com.projects.eudrwebapp.model.Enum.OrderStatus.COMPLETED
                        && order.getStatus() != com.projects.eudrwebapp.model.Enum.OrderStatus.CANCELLED)
                .toList();
        
        // Get high-risk active orders
        List<Order> highRiskActiveOrders = activeOrders.stream()
                .filter(order -> order.getRiskLevel() == com.projects.eudrwebapp.model.Enum.RiskLevel.HIGH)
                .limit(10) // Limit to top 10 for dashboard display
                .toList();
        
        // Calculate risk score statistics
        double averageRiskScore = customerOrders.stream()
                .mapToInt(order -> order.getRiskAssessment().getScore())
                .average()
                .orElse(0.0);
        
        int maxRiskScore = customerOrders.stream()
                .mapToInt(order -> order.getRiskAssessment().getScore())
                .max()
                .orElse(0);
        
        // Count orders with missing DDS
        long ordersWithMissingDDS = customerOrders.stream()
                .filter(order -> "Not Available".equals(order.getDdsStatus()))
                .count();
        
        // Calculate delivery timeline statistics
        java.time.LocalDate today = java.time.LocalDate.now();
        long thisWeekDeliveries = customerOrders.stream()
                .filter(order -> order.getEstimatedDeliveryDate() != null)
                .filter(order -> {
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(today, order.getEstimatedDeliveryDate());
                    return daysBetween >= 0 && daysBetween <= 7;
                })
                .count();
        
        long nextWeekDeliveries = customerOrders.stream()
                .filter(order -> order.getEstimatedDeliveryDate() != null)
                .filter(order -> {
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(today, order.getEstimatedDeliveryDate());
                    return daysBetween > 7 && daysBetween <= 14;
                })
                .count();
        
        long overdueDeliveries = customerOrders.stream()
                .filter(order -> order.getEstimatedDeliveryDate() != null)
                .filter(order -> {
                    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(today, order.getEstimatedDeliveryDate());
                    return daysBetween < 0;
                })
                .count();
        
        // Calculate supplier statistics
        long totalSuppliers = customerOrders.stream()
                .map(Order::getSupplier)
                .distinct()
                .count();
        
        // Calculate product category statistics
        java.util.Set<String> productCategories = customerOrders.stream()
                .map(Order::getProductCategory)
                .filter(category -> category != null && !category.isEmpty())
                .collect(java.util.stream.Collectors.toSet());
        
        // Calculate DDS compliance percentage
        int compliancePercentage = totalOrders > 0 ? 
            (int) Math.round((totalOrders - ordersWithMissingDDS) * 100.0 / totalOrders) : 100;
        
        // Calculate risk distribution percentages for progress bars
        int highRiskPercentage = totalOrders > 0 ? (int) Math.round(highRiskOrders * 100.0 / totalOrders) : 0;
        int mediumRiskPercentage = totalOrders > 0 ? (int) Math.round(mediumRiskOrders * 100.0 / totalOrders) : 0;
        int lowRiskPercentage = totalOrders > 0 ? (int) Math.round(lowRiskOrders * 100.0 / totalOrders) : 0;
        int unknownRiskPercentage = totalOrders > 0 ? (int) Math.round(unknownRiskOrders * 100.0 / totalOrders) : 0;
        
        // Add all data to model
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("highRiskOrders", highRiskOrders);
        model.addAttribute("mediumRiskOrders", mediumRiskOrders);
        model.addAttribute("lowRiskOrders", lowRiskOrders);
        model.addAttribute("unknownRiskOrders", unknownRiskOrders);
        model.addAttribute("activeOrders", activeOrders.size());
        model.addAttribute("highRiskActiveOrders", highRiskActiveOrders);
        model.addAttribute("averageRiskScore", Math.round(averageRiskScore * 100.0) / 100.0);
        model.addAttribute("maxRiskScore", maxRiskScore);
        model.addAttribute("ordersWithMissingDDS", ordersWithMissingDDS);
        model.addAttribute("allOrders", customerOrders);
        
        // Timeline statistics
        model.addAttribute("thisWeekDeliveries", thisWeekDeliveries);
        model.addAttribute("nextWeekDeliveries", nextWeekDeliveries);
        model.addAttribute("overdueDeliveries", overdueDeliveries);
        
        // Supplier statistics
        model.addAttribute("totalSuppliers", totalSuppliers);
        
        // Product category statistics
        model.addAttribute("productCategories", productCategories);
        
        // DDS compliance
        model.addAttribute("compliancePercentage", compliancePercentage);
        
        // Risk percentages for progress bars
        model.addAttribute("highRiskPercentage", highRiskPercentage);
        model.addAttribute("mediumRiskPercentage", mediumRiskPercentage);
        model.addAttribute("lowRiskPercentage", lowRiskPercentage);
        model.addAttribute("unknownRiskPercentage", unknownRiskPercentage);

        return "c-risk-overview";
    }

    @GetMapping("/manage-order/{orderId}")
    public String manageOrder(HttpSession session,
            Model model,
            @SessionAttribute(value = "userId", required = false) Long userId,
            @PathVariable("orderId") Long orderId) {

        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            return "redirect:/customer/deliveries/active";
        }
        model.addAttribute("order", order.get());
        return "c-manage-specific-order";
    }

    @PostMapping("/update-product-group/{orderId}")
    public String updateProductGroup(HttpSession session,
            @PathVariable("orderId") Long orderId,
            @RequestParam("productCategory") String productCategory,
            RedirectAttributes redirectAttributes) {

        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Order not found");
                return "redirect:/customer/deliveries/active";
            }

            Order order = orderOpt.get();

            // Validate product category
            List<String> validCategories = Arrays.asList(
                    "CATTLE", "COFFEE", "COCOA", "PALM_OIL", "SOY", "WOOD", "RUBBER");
            if (!validCategories.contains(productCategory.toUpperCase())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid product category selected");
                return "redirect:/customer/risk/manage-order/" + orderId;
            }

            // Update product category
            String oldCategory = order.getProductCategory();
            order.setProductCategory(productCategory.toUpperCase());

            // Trigger risk reassessment
            riskEngine.assessOrderRisk(order);
            orderRepository.save(order);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Product category updated from " + oldCategory + " to " + productCategory
                            + ". Risk assessment updated.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to update product category: " + e.getMessage());
        }

        return "redirect:/customer/risk/manage-order/" + orderId;
    }

    @PostMapping("/send-to-storage/{orderId}")
    public String sendToStorage(HttpSession session,
            @PathVariable("orderId") Long orderId,
            RedirectAttributes redirectAttributes) {

        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Order not found");
                return "redirect:/customer/deliveries/active";
            }

            Order order = orderOpt.get();

            // Set order status to indicate it's in storage
            order.setStatus(com.projects.eudrwebapp.model.Enum.OrderStatus.PENDING);

            // Add a note to the risk assessment hints
            order.getRiskAssessment().addItemToHint("storage", "Order sent to storage pending DDS resolution");

            orderRepository.save(order);

            redirectAttributes.addFlashAttribute("successMessage", "Order sent to storage successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send order to storage: " + e.getMessage());
        }

        return "redirect:/customer/risk/manage-order/" + orderId;
    }

    @PostMapping("/reroute-order/{orderId}")
    public String rerouteOrder(HttpSession session,
            @PathVariable("orderId") Long orderId,
            @RequestParam("newDestination") String newDestination,
            @RequestParam("rerouteReason") String rerouteReason,
            RedirectAttributes redirectAttributes) {

        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        try {
            Optional<Order> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Order not found");
                return "redirect:/customer/deliveries/active";
            }

            Order order = orderOpt.get();

            // Validate inputs
            if (newDestination == null || newDestination.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "New destination is required");
                return "redirect:/customer/risk/manage-order/" + orderId;
            }

            if (rerouteReason == null || rerouteReason.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Reroute reason is required");
                return "redirect:/customer/risk/manage-order/" + orderId;
            }

            // Find the new destination harbour
            Optional<Harbour> newHarbourOpt = harbourRepository.findByName(newDestination.trim());
            if (newHarbourOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Destination harbour '" + newDestination + "' not found");
                return "redirect:/customer/risk/manage-order/" + orderId;
            }

            // Update destination
            Harbour oldDestination = order.getDestination();
            Harbour newHarbour = newHarbourOpt.get();
            order.setDestination(newHarbour);

            // Add reroute information to hints
            order.getRiskAssessment().addItemToHint("reroute",
                    "Rerouted from " + oldDestination + " to " + newDestination + ". Reason: " + rerouteReason);

            // Trigger risk reassessment with new destination
            riskEngine.assessOrderRisk(order);
            orderRepository.save(order);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Order rerouted from " + oldDestination + " to " + newDestination);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reroute order: " + e.getMessage());
        }

        return "redirect:/customer/risk/manage-order/" + orderId;
    }
}
