package com.projects.eudrwebapp.service;

import com.projects.eudrwebapp.model.*;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public DataService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<CountryDeliveryDTO> getDeliveryCountryData(String userId) {
        System.out.println("Fetching user with ID: " + userId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            System.out.println("User not found for ID: " + userId);
            return List.of();
        }

        System.out.println("User found, fetching orders where user is customer...");
        // Fetch orders where the customer_id equals userId
        List<Order> orders = orderRepository.findByCustomerId(Long.parseLong(userId));
        System.out.println("Number of orders found: " + orders.size());

        // Map to count deliveries per supplier country
        Map<String, Long> deliveriesPerCountry = orders.stream()
                // filter out orders with null supplier or supplier location
                .filter(order -> order.getSupplier() != null && order.getSupplier().getLocation() != null)
                // group by supplier country name and count occurrences
                .collect(Collectors.groupingBy(
                        order -> order.getSupplier().getLocation().getIsoCode(),
                        Collectors.counting()
                ));

        deliveriesPerCountry.forEach((country, count) ->
                System.out.println("Country: " + country + " | Deliveries: " + count)
        );

        System.out.println("Preparing CountryDeliveryDTO list for return.");
        return deliveriesPerCountry.entrySet().stream()
                .map(entry -> new CountryDeliveryDTO(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }

    public Map<String, String> getDashboardData1(String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("activeOrders", String.valueOf(activeOrdersByCustomer(userId)));
        map.put("totalOrders", String.valueOf(completedOrdersByCustomer(userId)));
        return map;
    }

    public String activeOrdersByCustomer(String userId) {
        List<Order> orders = orderRepository.findByCustomerId(Long.valueOf(userId));
        return String.valueOf(orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.COMPLETED &&
                        order.getStatus() != OrderStatus.CANCELLED)
                .count());
    }

    public String completedOrdersByCustomer(String userId) {
        List<Order> allDel = orderRepository.findByCustomerId(Long.valueOf(userId));
        return String.valueOf(allDel.size());
    }

    public String highRiskDeliveriesByCustomer(String userId) {
        List<Order> allDel = orderRepository.findByCustomerId(Long.valueOf(userId));
        return String.valueOf(allDel.stream()
                .filter(order -> order.getRiskLevel() == RiskLevel.HIGH)
                .count());
    }
}

