package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@RequestMapping("/deliveries")
public class CDeliveriesController {

    private final OrderRepository orderRepository;

    public CDeliveriesController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public String deliveries(Model model, @SessionAttribute(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return "redirect:/user/login";
        }

        List<Order> deliveries = orderRepository.findByCustomerId(userId);

        model.addAttribute("deliveries", deliveries);

        return "c-deliveries";
    }

}
