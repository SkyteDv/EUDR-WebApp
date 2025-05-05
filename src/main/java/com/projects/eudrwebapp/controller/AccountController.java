package com.projects.eudrwebapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping
public class AccountController {

    private final UserRepository userRepository;

    public AccountController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/account")
    public String accountPage(HttpSession session, Model model) {
        
        if (session.getAttribute("userId") == null) {
            return "redirect:/user/login";
        }

        
        String userId = String.valueOf(session.getAttribute("userId"));
        User user = userRepository.getReferenceById(userId);
        model.addAttribute("user", user);

        return "c-account"; 
    }

    @GetMapping("/settings")
    public String settingsPage(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/user/login";
        }

        return "c-settings"; 
    }
}
