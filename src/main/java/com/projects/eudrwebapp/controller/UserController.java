package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.HelperService;
import com.projects.eudrwebapp.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final HelperService helperService;

    public UserController(UserRepository userRepository, OrderRepository orderRepository, OrderService orderService, HelperService helperService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.helperService = helperService;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Login Logic

    @GetMapping("login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("login")
    public String login(@ModelAttribute("user") User user, HttpSession session, Model model) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            if (user.getPassword().equals(dbUser.getPassword())) {
                session.setAttribute("userId", dbUser.getId());
                logger.info("User '{}' logged in successfully.", dbUser.getUsername());

                try {
                    InputStream inputStream = helperService.getInputStream("static/json/orders.json");

                    logger.info("Importing orders for Osapiens ID: {}", dbUser.getOsapiensID());
                    orderService.importOrders(inputStream, dbUser.getOsapiensID());

                    return "redirect:/dashboard";
                } catch (Exception e) {
                    logger.error("Error importing orders for user '{}'", dbUser.getUsername(), e);
                    model.addAttribute("loginError", "Error importing orders");
                    return "login";
                }
            } else {
                logger.warn("Login attempt failed for user '{}': Incorrect password.", user.getUsername());
                model.addAttribute("loginError", "Invalid password");
                return "login";
            }
        } else {
            logger.warn("Login attempt failed: Username '{}' not found.", user.getUsername());
            model.addAttribute("loginError", "Invalid username");
            return "login";
        }
    }



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Register Logic

    @GetMapping("register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("register")
    public String registerUser(@ModelAttribute User user, HttpSession session, Model model) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            logger.warn("Registration failed: User '{}' already exists.", user.getUsername());
            model.addAttribute("registerError", "User already exists");
            return "register";
        }

        userRepository.save(user);
        session.setAttribute("userId", user.getId());

        logger.info("New user registered: {}", user.getUsername());
        return "redirect:/user/registration-success?username=" + user.getUsername();
    }

    @GetMapping("registration-success")
    public String showRegistrationSuccess(@RequestParam("username") String username, Model model) {
        model.addAttribute("username", username);
        return "registration-success";
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Logout Logic

    @PostMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        logger.info("User session invalidated and logged out.");
        return "redirect:/";
    }
}
