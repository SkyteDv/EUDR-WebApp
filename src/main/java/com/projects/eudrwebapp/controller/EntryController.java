package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class EntryController {

    private final UserRepository userRepository;

    public EntryController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//Login Logic

    @GetMapping("login")
    public String login(Model model) {
        return "login";
    }


//Register Logic

    @GetMapping("register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("register")
    public String registerUser(@ModelAttribute User user, HttpSession session) {
        //saves user to h2 in memory repo
        userRepository.save(user);

        //set userId in the Session for future Database searches. Prolly not Save.
        session.setAttribute("userId", user.getId());
        return "redirect:/user/registration-success?username=" + user.getUsername();
    }

    @GetMapping("registration-success")
    public String showRegistrationSuccess(@RequestParam("username") String username, Model model) {
        model.addAttribute("username", username);
        return "registration-success";
    }
}