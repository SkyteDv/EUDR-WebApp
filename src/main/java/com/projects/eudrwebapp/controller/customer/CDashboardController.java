package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer/dashboard")
public class CDashboardController {

    private UserRepository userRepository;
    private AuthService authService;

    public CDashboardController(UserRepository userRepository,
                                AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping
    public String home(HttpSession session, Model model) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }
        String userid = String.valueOf(session.getAttribute("userId"));
        User user = userRepository.getReferenceById(userid);
        System.out.println("Logged in as: " + user);

        return "c-dashboard";
    }


}
