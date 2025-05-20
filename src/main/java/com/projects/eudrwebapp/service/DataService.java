package com.projects.eudrwebapp.service;

import com.projects.eudrwebapp.model.*;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final HelperService helperService;

    public DataService(OrderRepository orderRepository, UserRepository userRepository, HelperService helperService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.helperService = helperService;
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

    public List<String> getAllSupplierCountries(String userId, List<Order> orders) {
        List<String> countryIsoCodesDuplicated = new ArrayList<>();
        orders.stream()
                .filter(order -> order.getSupplier() != null && order.getSupplier().getLocation() != null)
                .forEach(order -> countryIsoCodesDuplicated.add(order.getSupplier().getLocation().getIsoCode()));

        List<String> countryIsoCodes = countryIsoCodesDuplicated.stream().distinct().collect(Collectors.toList());


        System.out.println("Countries with suppliers:");
        countryIsoCodes.forEach(System.out::println);

        return countryIsoCodes;
    }

    public Map<String, String> getGlobalDashboardData(String userId) {
        Map<String, String> map = new HashMap<>();
        List<Order> allDel = orderRepository.findByCustomerId(Long.valueOf(userId));
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return new HashMap<>();
        }
        User user = userOpt.get();

        map.put("deliveries.activeTotal", String.valueOf(activeOrders(allDel))); // More active orders globally
        map.put("deliveries.completedTotal", String.valueOf(totalActiveOrCompletedOrders(allDel))); // Placeholder or real
        map.put("deliveries.activeHighRisk", String.valueOf(highRiskDeliveries(allDel))); // Higher count of high-risk active deliveries
        map.put("deliveries.highRiskPercentage", formatPercentage(highRiskDeliveries(allDel), allDel.size())); // Higher risk percentage globally

        map.put("suppliers.activeTotal", String.valueOf(activeSuppliers(userId, user, allDel))); // Placeholder
        map.put("suppliers.historicalTotal", String.valueOf(totalSuppliers(userId, user,allDel))); // Placeholder

        map.put("dds.greenRate", String.valueOf(helperService.roundToPercentage(ddsGreenRate(allDel), 1))); // Placeholder
        map.put("dds.attachRate", String.valueOf(helperService.roundToPercentage(ddsAttachRate(allDel),1))); // Placeholder

        return map;
    }


    public Map<String, Map<String, String>> getCountryDashboardData(String userId) {
        Map<String, Map<String, String>> countryDataMap = new HashMap<>();
        List<Order> allDel = orderRepository.findByCustomerId(Long.valueOf(userId));
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return new HashMap<>();
        }
        User user = userOpt.get();

       List<String> countryIsoCodes = getAllSupplierCountries(userId, allDel);
       for (String country : countryIsoCodes) {
            List<Order> ordersFromThisCountry = allDel.stream().filter(order -> order.getSupplier().getLocation().getIsoCode().equalsIgnoreCase(country)).toList();
            ordersFromThisCountry.forEach(order -> {
                Map<String, String> data = new HashMap<>();
                data.put("deliveries.activeTotal", String.valueOf(activeOrders(ordersFromThisCountry))); // More active orders globally
                data.put("deliveries.completedTotal", String.valueOf(totalActiveOrCompletedOrders(ordersFromThisCountry))); // Placeholder or real
                data.put("deliveries.activeHighRisk", String.valueOf(highRiskDeliveries(ordersFromThisCountry))); // Higher count of high-risk active deliveries
                data.put("deliveries.highRiskPercentage", formatPercentage(highRiskDeliveries(ordersFromThisCountry), ordersFromThisCountry.size())); // Higher risk percentage globally

                data.put("suppliers.activeTotal", String.valueOf(activeSuppliers(userId, user, ordersFromThisCountry))); // Placeholder
                data.put("suppliers.historicalTotal", String.valueOf(totalSuppliers(userId, user, ordersFromThisCountry))); // Placeholder

                data.put("dds.greenRate", String.valueOf(helperService.roundToPercentage(ddsGreenRate(ordersFromThisCountry), 1))); // Placeholder
                data.put("dds.attachRate", String.valueOf(helperService.roundToPercentage(ddsAttachRate(ordersFromThisCountry), 1))); // Placeholder

                System.out.println("Data for Country: " + country);
                data.values().forEach(System.out::println);
                countryDataMap.put(country, data);
            });
        }
        return countryDataMap;
    }



    public long activeOrders(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.COMPLETED &&
                        order.getStatus() != OrderStatus.CANCELLED)
                .count();
    }

    public long totalActiveOrCompletedOrders(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .count();
    }

    public long highRiskDeliveries(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.COMPLETED &&
                        order.getStatus() != OrderStatus.CANCELLED)
                .filter(order -> order.getRiskLevel() == RiskLevel.HIGH)
                .count();
    }

    public String formatPercentage(long part, long total) {
        if (total == 0) return "0.0";
        return String.format("%.1f", (double) part / total * 100); // e.g., "23.45"
    }

    public int totalSuppliers(String userId, User user, List<Order> orders) {
        List<User> associatedUsers = new ArrayList<>();
        orders.stream().filter(order -> order.getCustomer() == user && !associatedUsers.contains(order.getSupplier())).forEach(order -> {
            associatedUsers.add(order.getSupplier());
        });
        return associatedUsers.size();
    }

    public int activeSuppliers(String userId, User user, List<Order> orders) {
        List<Order> activeOrders = orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED &&
                        order.getStatus() != OrderStatus.COMPLETED)
                .toList();

        List<User> associatedUsers = new ArrayList<>();
        orders.stream().filter(order -> order.getCustomer() == user && !associatedUsers.contains(order.getSupplier())).forEach(order -> {
           associatedUsers.add(order.getSupplier());
        });

        int count = 0;

        for (User supplier : associatedUsers) {
            boolean hasActiveOrder = activeOrders.stream()
                    .anyMatch(order -> order.getSupplier().equals(supplier));
            if (hasActiveOrder) {
                count++;
            }
        }
        return count;
    }

    public double ddsGreenRate(List<Order> orders) {
        double orders_with_dds = orders.stream()
                .filter(order -> !order.getDdsReferenceNumber()
                .equalsIgnoreCase(""))
                .count();
        return orders_with_dds / orders.size();
    }

    public double ddsAttachRate(List<Order> orders) {
        double attached_and_shipped = orders.stream()
                .filter(order -> order.getStatus()!=OrderStatus.PENDING &&
                        order.isDdsOnDeliveryNote())
                .count();
        double shipped = orders.stream()
                .filter(order -> order.getStatus()!=OrderStatus.PENDING)
                .count();

        if (shipped == 0) {
            System.out.println("No shipped orders found.");
            return 0.0;
        }
        if (attached_and_shipped == 0) {
            System.out.println("No shipped orders had DDS attached.");
        }
        return attached_and_shipped / shipped;
    }


}

