package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/suppliers")
public class CSupplierController {

    private UserRepository userRepository;

    public CSupplierController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String suppliers(Model model, @SessionAttribute(value = "userId", required = false) String userId) {
        List<User> suppliers = userRepository.findByUserType("SUPPLIER");
        if (userId == null) {
            return "redirect:/user/login";
        }
        Optional<User> loggedInUserOptional = userRepository.findById(userId);
        if (loggedInUserOptional.isEmpty()) {
            return "redirect:/user/login";
        }

        //Get UserReference
        User loggedInUser = loggedInUserOptional.get();

        //Get Assosicated Suppliers
        List<User> mySuppliers = loggedInUser.getSuppliers();

        //Filter all suppliers for associated ones
        List<User> filteredSuppliers = suppliers.stream()
                .filter(supplier -> !mySuppliers.contains(supplier))
                .toList();

        model.addAttribute("allSuppliers", filteredSuppliers);
        model.addAttribute("mySuppliers", mySuppliers);
        return "c-manage-suppliers";
    }

    @PostMapping("/select")
    @ResponseBody
    public String selectSupplier(@RequestBody Map<String, String> payload,
                                 @SessionAttribute(value = "userId", required = false) String userId) {
        String supplierId = payload.get("supplierId");
        System.out.println("Clicked supplier ID: " + supplierId);

        if (userId == null) {
            return "User not logged in";
        }

        Optional<User> loggedInUserOpt = userRepository.findById(userId);
        Optional<User> supplierOpt = userRepository.findById(supplierId);

        if (loggedInUserOpt.isEmpty() || supplierOpt.isEmpty()) {
            return "User or supplier not found";
        }

        User customer = loggedInUserOpt.get();
        User supplier = supplierOpt.get();

        // Prevent duplicates
        if (!customer.getSuppliers().contains(supplier)) {
            customer.getSuppliers().add(supplier);
            userRepository.save(customer);
            System.out.println("Customer updated: " + customer + ". Added Supplier: " + supplier);
            return "Supplier added successfully.";
        } else {
            return "Supplier already associated.";
        }
    }


}
