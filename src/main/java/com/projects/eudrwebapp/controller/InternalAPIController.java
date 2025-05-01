package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.HelperService;
import com.projects.eudrwebapp.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class InternalAPIController {

    private final UserRepository userRepository;
    private final HelperService helperService;

    public InternalAPIController(OrderService orderService, UserRepository userRepository, HelperService helperService) {
        this.userRepository = userRepository;
        this.helperService = helperService;
    }

    @PostMapping("/refresh-deliveries")
    public ResponseEntity<Map<String, String>> refreshDeliveries(HttpSession session) {
        System.out.println("Refresh deliveries");
        String userId = String.valueOf(session.getAttribute("userId"));

        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            try {
                User currentUser = user.get();
                String userType = currentUser.getUserType();
                String osapiensID = currentUser.getOsapiensID();
                helperService.updateDeliveries("/static/json/orders.json", osapiensID, userType);

                // Return a response with a message key
                Map<String, String> response = new HashMap<>();
                response.put("message", "Deliveries updated successfully");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Updating deliveries failed: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "User not found in session");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}