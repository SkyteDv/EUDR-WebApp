package com.projects.eudrwebapp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.riskAltertManagement.MailService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping
public class AccountController {

    private final UserRepository userRepository;
    @Autowired
    private MailService mailService;

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

        if ("SUPPLIER".equalsIgnoreCase(user.getUserType())) {
            return "s-account";
        } else {
            return "c-account";
        }
    }

    @GetMapping("/supplier/account")
    public String supplierAccountPage(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/user/login";
        }

        String userId = String.valueOf(session.getAttribute("userId"));
        User user = userRepository.getReferenceById(userId);
        model.addAttribute("user", user);

        return "s-account"; // Zeigt s-account.html an
    }

    @GetMapping("/settings")
    public String settingsPage(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/user/login";
        }

        return "settings";
    }

    @PostMapping("/account/personal/save")
    public String savePersonalInfo(@RequestParam String username,
            @RequestParam String osapiensID,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String userId = String.valueOf(session.getAttribute("userId"));
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/account";
        }

        User user = userOpt.get();
        user.setUsername(username);
        user.setOsapiensID(osapiensID);

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Changes saved successfully.");
        return "redirect:/account";
    }

    @PostMapping("/account/email/save")
    public String saveEmail(@RequestParam String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String userId = String.valueOf(session.getAttribute("userId"));
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/account";
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email address.");
            return "redirect:/account";
        }

        User user = userOpt.get();
        user.setEmail(email);
        userRepository.save(user);

        mailService.sendNotification(
                email,
                "Notification Email Saved",
                "Hello " + user.getUsername() + ",\n\nYour new notification email has been saved successfully.");

        redirectAttributes.addFlashAttribute("successMessage", "Email updated successfully.");
        return "redirect:/account#notifications";

    }

    @PostMapping("/account/password/change")
    public String changePassword(@RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String userId = String.valueOf(session.getAttribute("userId"));
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/account";
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(currentPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect.");
            return "redirect:/account";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
            return "redirect:/account";
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully.");
        return "redirect:/account";
    }

    @PostMapping("/account/delete")
    public String deleteAccount(@RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String userId = String.valueOf(session.getAttribute("userId"));
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/account";
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Incorrect password. Account was not deleted.");
            return "redirect:/account";
        }

        userRepository.delete(user);
        session.invalidate(); // log out the user
        return "redirect:/user/login?accountDeleted=true";
    }

}
