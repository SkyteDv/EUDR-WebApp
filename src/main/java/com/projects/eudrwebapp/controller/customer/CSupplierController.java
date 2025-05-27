package com.projects.eudrwebapp.controller.customer;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.DTO.SupplierStatsDTO;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.appAssistance.AuthService;
import com.projects.eudrwebapp.service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/suppliers")
public class CSupplierController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public CSupplierController(UserRepository userRepository, AuthService authService, OrderService orderService,
            OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public String suppliers(HttpSession session, Model model,
            @SessionAttribute(value = "userId", required = false) String userId) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        List<User> suppliers = userRepository.findByUserType("SUPPLIER");
        Optional<User> loggedInUserOptional = userRepository.findById(userId);

        if (loggedInUserOptional.isEmpty()) {
            return "redirect:/";
        }
        // Get UserReference
        User loggedInUser = loggedInUserOptional.get();

        // Get Assosicated Suppliers
        List<User> mySuppliers = loggedInUser.getAssociates();

        // Filter all suppliers for associated ones
        List<User> filteredSuppliers = suppliers.stream()
                .filter(supplier -> !mySuppliers.contains(supplier))
                .toList();

        System.out.println("Filtered sup: " + filteredSuppliers);
        System.out.println("My Sup: " + mySuppliers);
        model.addAttribute("allSuppliers", filteredSuppliers);
        model.addAttribute("mySuppliers", mySuppliers);
        return "c-manage-suppliers";
    }

    @GetMapping("/overview")
    public String supplierOverview(HttpSession session, Model model) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        String userId = String.valueOf(session.getAttribute("userId"));
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return "redirect:/";
        }

        User loggedInUser = userOpt.get();
        List<SupplierStatsDTO> supplierStats = orderService.getSupplierStatsForCustomer(loggedInUser);
        model.addAttribute("supplierStats", supplierStats);
        model.addAttribute("username", loggedInUser.getUsername());

        return "c-suppliers-overview"; // → HTML-Datei, die du gleich erstellst
    }

    @GetMapping("/details/{supplierId}")
    public String supplierDetail(@PathVariable Long supplierId, HttpSession session, Model model) {
        if (!authService.validateUserAuth(session, "CUSTOMER")) {
            return "redirect:/";
        }

        String userId = String.valueOf(session.getAttribute("userId"));
        Optional<User> customerOpt = userRepository.findById(userId);
        Optional<User> supplierOpt = userRepository.findById(String.valueOf(supplierId));

        if (customerOpt.isEmpty() || supplierOpt.isEmpty()) {
            return "redirect:/";
        }

        User customer = customerOpt.get();
        User supplier = supplierOpt.get();

        // Nur anzeigen, wenn Customer wirklich mit Supplier verbunden ist
        if (!customer.getAssociates().contains(supplier)) {
            return "redirect:/customer/suppliers/overview";
        }

        List<Order> deliveries = orderRepository.findByCustomerAndSupplier(customer, supplier);
        SupplierStatsDTO stats = orderService.getSupplierStatsForCustomerAndSupplier(customer, supplier);

        model.addAttribute("supplier", supplier);
        model.addAttribute("deliveries", deliveries);
        model.addAttribute("greenCount", stats.getGreenDeliveries());
        model.addAttribute("yellowCount", stats.getYellowDeliveries());
        model.addAttribute("redCount", stats.getRedDeliveries());

        return "c-supplier-details"; // neue HTML-Seite
    }

}
