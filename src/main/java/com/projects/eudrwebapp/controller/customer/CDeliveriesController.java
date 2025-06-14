package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.Enum.OrderStatus;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<Order> activeDeliveries = deliveries.stream()
                .filter(del -> del.getStatus() != OrderStatus.CANCELLED
                        && del.getStatus() != OrderStatus.COMPLETED)
                .toList();

        model.addAttribute("deliveries", activeDeliveries);

        return "c-deliveries";
    }

    @GetMapping("/history")
    public String deliveriesHistory(HttpSession session, Model model, @SessionAttribute(value = "userId", required = false) Long userId) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        List<Order> deliveries = orderRepository.findByCustomerId(userId);

        Random random = new Random();

        deliveries.stream()
                .filter(order -> order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.CANCELLED)
                .findAny()
                .ifPresent(order -> {
                    if ("Not Available".equals(order.getDdsStatus())) {
                        // DDS is effectively denied → cancel immediately
                        order.setStatus(OrderStatus.CANCELLED);
                    } else {
                        int roll = random.nextInt(6); // 0 to 3
                        if (roll == 0) {
                            order.setStatus(OrderStatus.CANCELLED); // 1 in 4
                        } else {
                            order.setStatus(OrderStatus.COMPLETED); // 3 in 4
                        }
                    }
                    orderRepository.save(order);
                });

        List<Order> historicDeliveries = deliveries.stream()
                .filter(del -> del.getStatus() == OrderStatus.CANCELLED
                        || del.getStatus() == OrderStatus.COMPLETED)
                .toList();

        model.addAttribute("deliveries", historicDeliveries);

        return "c-delivery-history";

    }

}
