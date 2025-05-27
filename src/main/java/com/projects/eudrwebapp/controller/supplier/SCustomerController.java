package com.projects.eudrwebapp.controller.supplier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.DTO.SupplierCustomerDTO;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/supplier/customers")
public class SCustomerController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public SCustomerController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/overview")
    public String showCustomersOverview(HttpSession session, Model model) {
        Long supplierId = (Long) session.getAttribute("userId");
        if (supplierId == null) {
            return "redirect:/user/login";
        }

        Optional<User> supplierOpt = userRepository.findById(supplierId.toString());
        if (supplierOpt.isEmpty()) {
            return "redirect:/user/login";
        }

        User supplier = supplierOpt.get();

        List<Order> allOrders = orderRepository.findAll();

        Map<Long, List<Order>> ordersByCustomer = allOrders.stream()
                .filter(order -> order.getSupplier().getId().equals(supplier.getId()))
                .collect(Collectors.groupingBy(order -> order.getCustomer().getId()));

        List<SupplierCustomerDTO> customerStats = new ArrayList<>();

        for (Map.Entry<Long, List<Order>> entry : ordersByCustomer.entrySet()) {
            Long customerId = entry.getKey();
            List<Order> customerOrders = entry.getValue();
            Optional<User> customerOpt = userRepository.findById(customerId.toString());

            if (customerOpt.isEmpty()) continue;
            User customer = customerOpt.get();

            int total = customerOrders.size();
            int ddsYes = (int) customerOrders.stream().filter(o -> "Yes".equalsIgnoreCase(o.getDdsStatus())).count();
            int ddsNo = (int) customerOrders.stream().filter(o -> "No".equalsIgnoreCase(o.getDdsStatus())).count();
            int ddsDenied = (int) customerOrders.stream().filter(o -> "Not Available".equalsIgnoreCase(o.getDdsStatus())).count();

            LocalDate lastDelivery = customerOrders.stream()
                    .map(Order::getOrderDate)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            LocalDate firstDelivery = customerOrders.stream()
                    .map(Order::getOrderDate)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            SupplierCustomerDTO dto = new SupplierCustomerDTO(
                    customer.getId(),
                    customer.getUsername(),
                    total,
                    lastDelivery,
                    ddsYes,
                    ddsNo,
                    ddsDenied,
                    customer.getLocation() != null ? customer.getLocation().name() : "-",
                    firstDelivery
            );

            customerStats.add(dto);
        }

        model.addAttribute("customerStats", customerStats);
        return "s-customers-overview";
    }
}