package com.projects.eudrwebapp.controller;

import com.google.zxing.WriterException;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.HelperService;
import com.projects.eudrwebapp.service.OrderService;
import com.projects.eudrwebapp.service.QRCodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class InternalAPIController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final HelperService helperService;
    private final QRCodeService qrCodeService;

    public InternalAPIController(OrderService orderService, UserRepository userRepository, HelperService helperService, QRCodeService qrCodeService, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.helperService = helperService;
        this.qrCodeService = qrCodeService;
        this.orderRepository = orderRepository;
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

    @GetMapping("/generate-qr/{id}")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long id) {
        try {
            // Generate the QR code using the QRCodeService
            Optional<Order> order = orderRepository.findById(id);
            if (order.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Order currentOrder = order.get();
            String ddsRef = currentOrder.getDdsReferenceNumber();
            byte[] qrCodeImage = qrCodeService.generateQRCodeImage(ddsRef, 200, 200);

            // Prepare headers to trigger download in the browser
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");
            headers.add("Content-Disposition", "attachment; filename=\"qr-code.png\"");

            // Return the image as a byte array with the headers
            return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);

        } catch (WriterException | IOException e) {
            // Handle errors (e.g., QR code generation failure)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating QR code: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/deliveries/attached/{ddsReferenceNumber}")
    public ResponseEntity<Void> deliveriesAttached(@PathVariable String ddsReferenceNumber) {
        Optional<Order> optOrder = orderRepository.findByDdsReferenceNumber(ddsReferenceNumber);
        if (optOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Order currentOrder = optOrder.get();
        boolean updated = orderRepository.markDDSAsAttached(currentOrder.getDdsReferenceNumber()) == 1;
        System.out.println("Updated: " + updated);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }


}