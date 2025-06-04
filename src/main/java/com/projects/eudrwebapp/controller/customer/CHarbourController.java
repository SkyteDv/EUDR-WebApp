package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.Harbour;
import com.projects.eudrwebapp.repository.HarbourRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("customer/harbour/")
public class CHarbourController {
    HarbourRepository harbourRepository;
    AuthService authService;

    public CHarbourController(HarbourRepository harbourRepository, AuthService authService) {
        this.harbourRepository = harbourRepository;
        this.authService = authService;
    }

    @GetMapping("overview")
    public String harbour(HttpSession session) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }
        return "c-manage-harbours";
    }

    @GetMapping("details/{harbourName}")
    public String details(HttpSession session, @PathVariable String harbourName, Model model) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }
        Optional<Harbour> optHarbour = harbourRepository.findByName(harbourName);
        if (optHarbour.isEmpty()) {
            return "redirect:/customer/harbour/overview";
        }
        Harbour harbour = optHarbour.get();
        model.addAttribute("harbour", harbour);
        return "c-harbour-details";
    }

}
