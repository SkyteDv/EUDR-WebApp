package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import com.projects.eudrwebapp.service.riskAltertManagement.RiskEngine;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("customer/risk")
public class CRiskController {

    AuthService authService;
    UserRepository userRepository;
    OrderRepository orderRepository;
    RiskEngine riskEngine;

    public CRiskController(AuthService authService, UserRepository userRepository, OrderRepository orderRepository, RiskEngine riskEngine) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.riskEngine = riskEngine;
    }

    @GetMapping("/overview")
    public String overview(HttpSession session, Model model) {
        if(!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        return "c-risk-overview";
    }

    @GetMapping("/manage-order/{orderId}")
    public String manageOrder(HttpSession session,
                              Model model,
                              @SessionAttribute(value = "userId", required = false) Long userId,
                              @PathVariable("orderId") Long orderId) {

        if(!authService.validateUserAuth(session, "CUSTOMER")) {
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

        if(!authService.validateUserAuth(session, "CUSTOMER")) {
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
                "CATTLE", "COFFEE", "COCOA", "PALM_OIL", "SOY", "WOOD", "RUBBER"
            );
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
                "Product category updated from " + oldCategory + " to " + productCategory + ". Risk assessment updated.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update product category: " + e.getMessage());
        }

        return "redirect:/customer/risk/manage-order/" + orderId;
    }

    @PostMapping("/send-to-storage/{orderId}")
    public String sendToStorage(HttpSession session,
                                @PathVariable("orderId") Long orderId,
                                RedirectAttributes redirectAttributes) {

        if(!authService.validateUserAuth(session, "CUSTOMER")) {
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

        if(!authService.validateUserAuth(session, "CUSTOMER")) {
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

            // Update destination
            String oldDestination = order.getDestination();
            order.setDestination(newDestination);
            
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
