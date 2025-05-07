package com.projects.eudrwebapp.controller;

import com.google.zxing.WriterException;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.OrderStatus;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.HelperService;
import com.projects.eudrwebapp.service.OrderService;
import com.projects.eudrwebapp.service.PDFService;
import com.projects.eudrwebapp.service.QRCodeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
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
    private final PDFService pdfService;

    public InternalAPIController(OrderService orderService,
                                 UserRepository userRepository,
                                 HelperService helperService,
                                 QRCodeService qrCodeService,
                                 OrderRepository orderRepository,
                                 PDFService pdfService) {
        this.userRepository = userRepository;
        this.helperService = helperService;
        this.qrCodeService = qrCodeService;
        this.orderRepository = orderRepository;
        this.pdfService = pdfService;
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

    @GetMapping("/generate-order-pdf/{id}")
    public ResponseEntity<byte[]> generateOrderPDF(@PathVariable Long id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Order order = orderOptional.get();
        try {
            BufferedImage qrImage = qrCodeService.generateQRCodeBufferedImage(order.getDdsReferenceNumber(), 200, 200);
            byte[] pdf = pdfService.generateOrderPDFWithQRCode(order, qrImage);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/pdf");
            headers.add("Content-Disposition", "attachment; filename=\"order-summary.pdf\"");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
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

    @PostMapping("/deliveries/update/status")
    public ResponseEntity<Map<String, String>> updateOrderStatus(@RequestBody Map<String, String> payload) {
        System.out.println("Received request to update order status with payload: " + payload);

        try {
            Long orderId = Long.parseLong(payload.get("orderId"));
            String newStatus = payload.get("status");

            System.out.println("Parsed orderId: " + orderId);
            System.out.println("Requested new status: " + newStatus);

            Optional<Order> optOrder = orderRepository.findById(orderId);
            if (optOrder.isEmpty()) {
                System.out.println("Order not found with ID: " + orderId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Order not found"));
            }

            Order order = optOrder.get();

            try {
                OrderStatus statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());
                order.setStatus(statusEnum);
                System.out.println("Order status set to: " + statusEnum);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status provided: " + newStatus);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid order status: " + newStatus));
            }

            orderRepository.save(order);

            return ResponseEntity.ok(Map.of("message", "Order status updated successfully"));
        } catch (Exception e) {
            System.out.println("Exception occurred while updating order status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to update order status: " + e.getMessage()));
        }
    }




}