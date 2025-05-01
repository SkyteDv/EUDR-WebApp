package com.projects.eudrwebapp.controller.customer;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer/suppliers")
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
