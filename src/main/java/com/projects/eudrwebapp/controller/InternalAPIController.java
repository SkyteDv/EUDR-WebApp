package com.projects.eudrwebapp.controller;

import com.projects.eudrwebapp.model.*;
import com.projects.eudrwebapp.repository.ImportStatusRepository;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import com.projects.eudrwebapp.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class InternalAPIController {

    private static final Logger logger = LoggerFactory.getLogger(InternalAPIController.class);

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ImportStatusRepository importStatusRepository;
    private final HelperService helperService;
    private final QRCodeService qrCodeService;
    private final PDFService pdfService;
    private final OrderService orderService;
    private final DataService dataService;
    private final HttpSession httpSession;

    public InternalAPIController(OrderService orderService,
                                 UserRepository userRepository,
                                 HelperService helperService,
                                 QRCodeService qrCodeService,
                                 OrderRepository orderRepository,
                                 PDFService pdfService,
                                 DataService dataService,
                                 HttpSession httpSession,
                                 ImportStatusRepository importStatusRepository) {
        this.userRepository = userRepository;
        this.helperService = helperService;
        this.qrCodeService = qrCodeService;
        this.orderRepository = orderRepository;
        this.pdfService = pdfService;
        this.orderService = orderService;
        this.dataService = dataService;
        this.httpSession = httpSession;
        this.importStatusRepository = importStatusRepository;
    }

    @GetMapping("/import-status")
    public ResponseEntity<Boolean> isImportDone(HttpSession session) {
        String userId = String.valueOf(session.getAttribute("userId"));
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        User user = userOpt.get();
        String osapiensId = user.getOsapiensID();
        boolean isDone = importStatusRepository.findById(osapiensId)
                .map(ImportStatus::isCompleted)
                .orElse(false);

        return ResponseEntity.ok(isDone);
    }

    @PostMapping("/refresh-deliveries")
    public ResponseEntity<Map<String, String>> refreshDeliveries(HttpSession session) {
        String userId = String.valueOf(session.getAttribute("userId"));
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found in session");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User currentUser = userOpt.get();
        String userType = currentUser.getUserType();
        String osapiensID = currentUser.getOsapiensID();

        try {
            // Mark import as not completed at start
            ImportStatus status = importStatusRepository.findById(userId)
                    .orElse(new ImportStatus(userId, false, LocalDateTime.now()));
            status.setCompleted(false);
            status.setLastUpdated(LocalDateTime.now());
            importStatusRepository.save(status);

            session.setAttribute("lastFetchTime", System.currentTimeMillis());

            // Run the update (can be async or sync)
            helperService.updateDeliveries("/static/json/orders.json", osapiensID, userType);

            // After successful update, mark completed
            status.setCompleted(true);
            status.setLastUpdated(LocalDateTime.now());
            importStatusRepository.save(status);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Deliveries updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Updating deliveries failed: " + e.getMessage());

            // Optional: set import status to false/failure state
            importStatusRepository.findById(userId).ifPresent(s -> {
                s.setCompleted(false);
                s.setLastUpdated(LocalDateTime.now());
                importStatusRepository.save(s);
            });

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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

    @PostMapping("/deliveries/attached/{orderId}")
    public ResponseEntity<Void> deliveriesAttached(@PathVariable String orderId) {
        Long lOrderId = Long.parseLong(orderId);
        Optional<Order> optOrder = orderRepository.findById(lOrderId);
        if (optOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Order currentOrder = optOrder.get();
        boolean updated = orderRepository.markDDSAsAttached(lOrderId) == 1;
        System.out.println("Updated: " + updated);
        System.out.println("Current DDSOnDel: " + currentOrder.getDdsReferenceNumber());
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

                // Your custom risk logic: if shipped but DDS missing, increase risk level
                if (statusEnum == OrderStatus.SHIPPED && !order.isDdsOnDeliveryNote()) {
                    order.setRiskLevel(order.getRiskLevel().increase());
                    System.out.println("Risk level increased to MEDIUM due to missing DDS on delivery note when sent.");
                }

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

    @GetMapping("/geo-data")
    public List<CountryDeliveryDTO> getGeoData(HttpSession session) throws Exception {
        String userid = String.valueOf(session.getAttribute("userId"));
        logger.info("Fetching geo-data for userId: {}", userid);

        List<CountryDeliveryDTO> dataList = dataService.getDeliveryCountryData(userid);

        if (dataList.isEmpty()) {
            logger.info("No delivery data found for userId: {}", userid);
            return List.of();
        }

        logger.info("Returning {} delivery data entries for userId: {}", dataList.size(), userid);
        return dataList;
    }

    @GetMapping("/dashboard-data/country-data")
    public ResponseEntity<Map<String, Map<String, String>>> getCountryData(HttpSession session) {
        String userid = String.valueOf(session.getAttribute("userId"));

        Map<String, String> global = dataService.getGlobalDashboardData(userid);
        Map<String, Map<String, String>> dataMap = dataService.getCountryDashboardData(userid);

        dataMap.put("GLOBAL", global);

        return ResponseEntity.ok(dataMap);
    }


}