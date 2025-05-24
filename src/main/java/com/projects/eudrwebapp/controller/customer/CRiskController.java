package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Optional;

@Controller
@RequestMapping("customer/risk")
public class CRiskController {

    AuthService authService;
    UserRepository userRepository;
    OrderRepository orderRepository;

    public CRiskController(AuthService authService, UserRepository userRepository, OrderRepository orderRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
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


}
