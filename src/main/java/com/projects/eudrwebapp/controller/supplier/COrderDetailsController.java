package com.projects.eudrwebapp.controller.supplier;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("supplier/deliveries/details")
public class COrderDetailsController {

    OrderRepository orderRepository;
    AuthService authService;

    public COrderDetailsController(OrderRepository orderRepository, AuthService authService) {
        this.orderRepository = orderRepository;
        this.authService = authService;
    }

    @GetMapping("/{id}")
    public String details(HttpSession session, Model model, @PathVariable Long id) {
        if(!authService.validateUserAuth(session, "SUPPLIER"))  {
            return "redirect:/";
        }
        Optional<Order> order = orderRepository.findById(id);
        order.ifPresent(value -> model.addAttribute("order", value));


        return "s-order-details";
    }

}
