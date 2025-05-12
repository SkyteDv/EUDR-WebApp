package com.projects.eudrwebapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.OrderStatus;
import com.projects.eudrwebapp.model.SupplierStatsDTO;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

//  Constructor injection (no need for @Autowired, Spring will inject this automatically)
    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Async
    @Transactional
    public void importOrders(InputStream inputStream, String osapiensID, String fetchingFor) throws Exception {
        long startTime = System.currentTimeMillis(); // Start timer

        List<Map<String, Object>> ordersData = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});

        int createdOrders = 0;
        int skippedDueToWrongUser = 0;
        int skippedDueToDuplicateDelivery = 0;
        int skippedDueToMissingUsers = 0;
        int skippedAssociationCreation = 0;
        int createdAssociations = 0;

        for (Map<String, Object> orderData : ordersData) {
            String supplierOsapiensID = (String) orderData.get("supplierOsapiensID");
            String customerOsapiensID = (String) orderData.get("customerOsapiensID");
            String erpReferenceNumber = (String) orderData.get("erpReferenceNumber");

            if (!customerOsapiensID.equals(osapiensID) && fetchingFor.equalsIgnoreCase("CUSTOMER")) {
                skippedDueToWrongUser++;
                continue;
            }

            if (!supplierOsapiensID.equals(osapiensID) && fetchingFor.equalsIgnoreCase("SUPPLIER")) {
                skippedDueToWrongUser++;
                continue;
            }



            if (orderRepository.findByErpReferenceNumber(erpReferenceNumber).isPresent()) {
                skippedDueToDuplicateDelivery++;
                continue;
            }

            Optional<User> supplierOpt = userRepository.findByOsapiensID(supplierOsapiensID);
            Optional<User> customerOpt = userRepository.findByOsapiensID(customerOsapiensID);

            if (supplierOpt.isEmpty() || customerOpt.isEmpty()) {
                skippedDueToMissingUsers++;
                continue;
            }

            User supplierUser = supplierOpt.get();
            User customerUser = customerOpt.get();

            if (!customerUser.getAssociates().contains(supplierUser)) {
                customerUser.getAssociates().add(supplierUser);
                userRepository.save(customerUser);
                createdAssociations++;
            } else {
                skippedAssociationCreation++;
            }

            Order order = new Order(
                    erpReferenceNumber,
                    (String) orderData.get("productCategory"),
                    (String) orderData.get("productName"),
                    (String) orderData.get("dimensions"),
                    (String) orderData.get("destination"),
                    LocalDate.parse((String) orderData.get("orderDate")),
                    LocalDate.parse((String) orderData.get("estimatedDeliveryDate")),
                    (String) orderData.get("ddsReferenceNumber"),
                    false,
                    OrderStatus.PENDING,
                    supplierUser,
                    customerUser,
                    (String) orderData.get("responsible_party")
            );
            orderRepository.save(order);
            createdOrders++;
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("=== Import Performance Log ===");
        System.out.println("Total Orders in Input: " + ordersData.size());
        System.out.println("Created Orders: " + createdOrders);
        System.out.println("Created Associations: " + createdAssociations);
        System.out.println("Skipped due to wrong User: " + skippedDueToWrongUser);
        System.out.println("Skipped due to duplicate delivery (ERP ref): " + skippedDueToDuplicateDelivery);
        System.out.println("Skipped due to missing supplier/customer user: " + skippedDueToMissingUsers);
        System.out.println("Skipped association creation: " + skippedAssociationCreation);
        System.out.println("Total Time: " + duration + " ms");
        System.out.println("===============================");
    }

    public List<SupplierStatsDTO> getSupplierStatsForCustomer(User customer) {
    List<Order> orders = orderRepository.findAll(); // Optional: effizienter mit eigenem Query für Customer

    // Filter: nur Orders dieses Customers
    List<Order> customerOrders = orders.stream()
            .filter(o -> o.getCustomer().getId().equals(customer.getId()))
            .toList();

    // Gruppiere nach Supplier
    Map<User, List<Order>> ordersBySupplier = customerOrders.stream()
            .collect(Collectors.groupingBy(Order::getSupplier));

    // Erzeuge DTOs
    List<SupplierStatsDTO> result = new ArrayList<>();

    for (Map.Entry<User, List<Order>> entry : ordersBySupplier.entrySet()) {
        User supplier = entry.getKey();
        List<Order> supplierOrders = entry.getValue();

        long total = supplierOrders.size();
        long green = supplierOrders.stream()
                .filter(o -> o.getDdsStatus().equals("Yes"))
                .count();
        long red = supplierOrders.stream()
                .filter(o -> o.getDdsStatus().equals("Not Available"))
                .count();
        long yellow = supplierOrders.stream()
                .filter(o -> o.getDdsStatus().equals("No"))
                .count();

        SupplierStatsDTO dto = new SupplierStatsDTO(
                supplier.getUsername(),
                supplier.getId(),
                total,
                green,
                red,
                yellow
        );

        result.add(dto);
    }

    return result;
}

}


