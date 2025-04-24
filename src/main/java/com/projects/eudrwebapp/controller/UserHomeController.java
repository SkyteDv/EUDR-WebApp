package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class UserHomeController {

    private UserRepository userRepository;

    public UserHomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String home(HttpSession session, Model model) {
        String userid = String.valueOf(session.getAttribute("userId"));
        User user = userRepository.getReferenceById(userid);
        System.out.println(user);
        String userType = user.getUserType();

        if (userType.equalsIgnoreCase("CUSTOMER")) {
            return "home-customer";
        } else if (userType.equalsIgnoreCase("SUPPLIER")) {
            return "home-supplier";
        } else {
            return "redirect:/";
        }
    }


}
