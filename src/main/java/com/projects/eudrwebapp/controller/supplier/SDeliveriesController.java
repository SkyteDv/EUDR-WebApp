package com.projects.eudrwebapp.controller.supplier;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("supplier/deliveries")
public class SDeliveriesController {

    OrderRepository orderRepository;
    AuthService authService;

    public SDeliveriesController(OrderRepository orderRepository, AuthService authService) {
        this.orderRepository = orderRepository;
        this.authService = authService;
    }

    @GetMapping
    public String deliveries(Model model, HttpSession session) {
        if (!authService.validateUserAuth(session, "SUPPLIER")) {
            return "redirect:/";
        }
        Long userId = (Long) session.getAttribute("userId");
        List<Order> deliveries = orderRepository.findBySupplierId(userId);

        List<Order> deliveries_Attached = deliveries.stream().filter(Order::isDdsOnDeliveryNote).toList();
        List<Order> deliveries_NotAttached = deliveries.stream().filter(order -> !order.isDdsOnDeliveryNote()).toList();

        System.out.println("Attached: " + deliveries_Attached.size());
        System.out.println("NOT Attached: " +deliveries_NotAttached.size());

        model.addAttribute("activeDeliveries", deliveries_NotAttached);
        model.addAttribute("attachedDeliveries", deliveries_Attached);
        return "s-deliveries";
    }


}
