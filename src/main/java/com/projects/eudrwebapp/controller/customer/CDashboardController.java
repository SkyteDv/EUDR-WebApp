package com.projects.eudrwebapp.controller.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import com.projects.eudrwebapp.service.appAssistance.DataService;
import com.projects.eudrwebapp.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer/dashboard")
public class CDashboardController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final OrderService orderService;
    private final DataService dataService;

    public CDashboardController(UserRepository userRepository,
                                AuthService authService,
                                OrderService orderService,
                                DataService dataService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.orderService = orderService;
        this.dataService = dataService;
    }

    @GetMapping
    public String home(HttpSession session, Model model) throws JsonProcessingException {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }
        return "c-dashboard";
    }


}
