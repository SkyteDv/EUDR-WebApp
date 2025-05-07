package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer/suppliers")
public class CSupplierController {

    private final UserRepository userRepository;
    private final AuthService authService;

    public CSupplierController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping
    public String suppliers(HttpSession session, Model model, @SessionAttribute(value = "userId", required = false) String userId) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        List<User> suppliers = userRepository.findByUserType("SUPPLIER");
        Optional<User> loggedInUserOptional = userRepository.findById(userId);

        if (loggedInUserOptional.isEmpty()) {
            return "redirect:/";
        }
        //Get UserReference
        User loggedInUser = loggedInUserOptional.get();

        //Get Assosicated Suppliers
        List<User> mySuppliers = loggedInUser.getAssociates();

        //Filter all suppliers for associated ones
        List<User> filteredSuppliers = suppliers.stream()
                .filter(supplier -> !mySuppliers.contains(supplier))
                .toList();

        System.out.println("Filtered sup: " + filteredSuppliers);
        System.out.println("My Sup: " + mySuppliers);
        model.addAttribute("allSuppliers", filteredSuppliers);
        model.addAttribute("mySuppliers", mySuppliers);
        return "c-manage-suppliers";
    }


}
