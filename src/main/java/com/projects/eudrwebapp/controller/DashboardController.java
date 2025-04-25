package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.SessionService;
import com.projects.eudrwebapp.service.URLService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private UserRepository userRepository;
    private SessionService sessionService;
    private URLService urlService;

    public DashboardController(UserRepository userRepository, SessionService sessionService, URLService urlService) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.urlService = urlService;
    }

    @GetMapping
    public String home(HttpSession session, Model model) {

        if (!sessionService.isLoggedIn(session)) {
            return urlService.goHome();
        }

        String userid = String.valueOf(session.getAttribute("userId"));
        User user = userRepository.getReferenceById(userid);
        System.out.println("Logged in as: " + user);
        String userType = user.getUserType();

        if (userType.equalsIgnoreCase("CUSTOMER")) {
            return "c-dashboard";
        } else if (userType.equalsIgnoreCase("SUPPLIER")) {
            return "s-dashboard";
        } else {
            return "redirect:/";
        }
    }


}
