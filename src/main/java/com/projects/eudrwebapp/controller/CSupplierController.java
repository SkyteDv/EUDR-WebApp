package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class CSupplierController {

    private UserRepository userRepository;

    public CSupplierController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String suppliers(Model model) {
        List<User> suppliers = userRepository.findByUserType("SUPPLIER");
        suppliers.forEach(System.out::println);
        model.addAttribute("suppliers", suppliers);
        return "c-manage-suppliers";
    }

}
