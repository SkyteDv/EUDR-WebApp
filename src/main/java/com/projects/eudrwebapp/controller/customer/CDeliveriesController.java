package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@RequestMapping("/customer/deliveries")
public class CDeliveriesController {

    private final OrderRepository orderRepository;
    private final AuthService authService;

    public CDeliveriesController(OrderRepository orderRepository, AuthService authService) {
        this.orderRepository = orderRepository;
        this.authService = authService;
    }

    @GetMapping("/active")
    public String deliveries(HttpSession session, Model model, @SessionAttribute(value = "userId", required = false) Long userId) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        List<Order> deliveries = orderRepository.findByCustomerId(userId);

        model.addAttribute("deliveries", deliveries);

        return "c-deliveries";
    }

    @GetMapping("/history")
    public String deliveriesHistory(HttpSession session, Model model, @SessionAttribute(value = "userId", required = false) Long userId) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        List<Order> deliveries = orderRepository.findByCustomerId(userId);

        //List<Order> history = orderRepository.
        return "c-delivery-history";

    }

}
