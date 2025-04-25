package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//Login Logic

    @GetMapping("login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("login")
    public String login(@ModelAttribute("user") User user, HttpSession session, Model model) {
        // Retrieve user by username
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            User dbUser = existingUser.get();

            if (user.getPassword().equals(dbUser.getPassword())) {
                // Passwords match
                session.setAttribute("userId", dbUser.getId());
                System.out.println("Correct Password");
                return "redirect:/dashboard"; // Redirect to dashboard on successful login
            } else {
                // Invalid password
                System.out.println("Wrong password");
                model.addAttribute("loginError", "Invalid password");
                return "login"; // Return to login page with error
            }
        } else {
            // Invalid username
            model.addAttribute("loginError", "Invalid username");
            return "login"; // Return to login page with error
        }
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//Register Logic

    @GetMapping("register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("register")
    public String registerUser(@ModelAttribute User user, HttpSession session, Model model) {

        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            System.out.println("User already exists");
            model.addAttribute("registerError", "User already exists");
            return "register";
        }

        System.out.println("New User: " + user);
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


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//Logout Logic

    @PostMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        System.out.println("Session Invalidated");
        return "redirect:/";
    }

}