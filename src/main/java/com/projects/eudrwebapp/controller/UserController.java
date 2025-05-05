package com.projects.eudrwebapp.controller;

import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.HelperService;
import com.projects.eudrwebapp.service.OrderService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
    public String login(HttpSession session, Model model, @CookieValue(value = "rememberMe", required = false) String rememberedUserId) {
        if (rememberedUserId != null) {
            Optional<User> possibleUser = userRepository.findById(rememberedUserId);
            if (possibleUser.isPresent()) {
                User user = possibleUser.get();
                String userType = user.getUserType();
                Long userId = user.getId();
                session.setAttribute("userId", userId);
                System.out.println("Remembered User Id: " + userId);
                if (userType.equals("CUSTOMER")) {
                    return "redirect:/customer/dashboard";
                } else if (userType.equalsIgnoreCase("SUPPLIER")) {
                    return "redirect:/supplier/dashboard";
                } else {
                    return "login";
                }
            }
        }
        model.addAttribute("user", new User());
        return "login";
    }


    @PostMapping("/login")
    public String login(
            @ModelAttribute("user") User user,
            HttpSession session,
            Model model,
            @RequestParam(required = false) String rememberMe,
            HttpServletResponse response) {

        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            if (user.getPassword().equals(dbUser.getPassword())) {
                session.setAttribute("userId", dbUser.getId());
                session.setAttribute("username", dbUser.getUsername()); //Username in Session, ich hole mir den für die Account ansicht.
                logger.info("User '{}' logged in successfully.", dbUser.getUsername());

                try {
                    InputStream inputStream = helperService.getInputStream("static/json/orders.json");
                    String userType = dbUser.getUserType();
                    logger.info("Importing orders for Osapiens ID: {}", dbUser.getOsapiensID());
                    orderService.importOrders(inputStream, dbUser.getOsapiensID(), userType);

                    // Set rememberMe cookie for 2 minutes if checkbox was selected
                    if (rememberMe != null && rememberMe.equalsIgnoreCase("on")) {
                        Cookie cookie = new Cookie("rememberMe", String.valueOf(dbUser.getId()));
                        cookie.setMaxAge(2 * 60); // 2 minutes
                        cookie.setPath("/");
                        cookie.setHttpOnly(true);
                        response.addCookie(cookie);
                    }

                    if (userType.equalsIgnoreCase("customer")) {
                        return "redirect:/customer/dashboard";
                    } else if(userType.equalsIgnoreCase("supplier")) {
                        return "redirect:/supplier/dashboard";
                    } else {
                        return "redirect:/";
                    }
                } catch (Exception e) {
                    logger.error("Error importing orders for user '{}'", dbUser.getUsername(), e);
                    model.addAttribute("loginError", "Error importing orders");
                    return "login";
                }
            } else {
                logger.warn("Login failed for '{}': wrong password", user.getUsername());
                model.addAttribute("loginError", "Invalid password");
                return "login";
            }
        } else {
            logger.warn("Login failed: user '{}' not found", user.getUsername());
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
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        logger.info("User session invalidated and logged out.");

        // Remove the rememberMe cookie
        Cookie cookie = new Cookie("rememberMe", null);
        cookie.setMaxAge(0); // Deletes the cookie
        cookie.setPath("/"); // Must match the original path
        response.addCookie(cookie);
        logger.info("All Logging Cookies Removed");

        return "redirect:/";
    }
}
