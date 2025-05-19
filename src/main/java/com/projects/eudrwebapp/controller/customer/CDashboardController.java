package com.projects.eudrwebapp.controller.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.eudrwebapp.model.CountryDeliveryDTO;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.AuthService;
import com.projects.eudrwebapp.service.DataService;
import com.projects.eudrwebapp.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

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
        String userid = String.valueOf(session.getAttribute("userId"));

        Map<String, String> data = dataService.getDashboardData1(userid);


        return "c-dashboard";
    }


}
