package com.projects.eudrwebapp.configuration;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.OrderStatus;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Configuration
public class H2DataLoader {

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, OrderRepository orderRepository) {
        return args -> {
            // Insert default suppliers (reduced to 5)
            userRepository.save(new User("GreenTechSupply", "password123", "SUPPLIER"));
            userRepository.save(new User("AlphaMaterials", "password123", "SUPPLIER"));
            userRepository.save(new User("BlueSkyLogistics", "password123", "SUPPLIER"));
            userRepository.save(new User("SolarisManufacturing", "password123", "SUPPLIER"));
            userRepository.save(new User("TechnoWarehouse", "password123", "SUPPLIER"));

            // Insert a customer
            User customer = new User("1", "1", "CUSTOMER");
            userRepository.save(customer);

            // Get the list of suppliers
            User supplier1 = userRepository.findByUsername("GreenTechSupply").orElseThrow();
            User supplier2 = userRepository.findByUsername("AlphaMaterials").orElseThrow();
            User supplier3 = userRepository.findByUsername("BlueSkyLogistics").orElseThrow();

            // Assign the suppliers to the customer
            customer.setSuppliers(Arrays.asList(supplier1, supplier2, supplier3));
            userRepository.save(customer);



            List<Order> orders = List.of(
                    new Order("RNR4807140", "Beef", "Steak", "Large", "Rotterdam Hafen",
                            LocalDate.now(), LocalDate.now().plusDays(10), "DDS875421", true,
                            OrderStatus.IN_HARBOUR, supplier1, customer),
                    new Order("RNR4898163", "Soja", "Soybeans", "Medium", "Hamburg Port",
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(8), "DDS654322", false,
                            OrderStatus.SHIPPED, supplier1, customer),
                    new Order("RNR3867142", "Wood", "Logs", "Large", "Berlin Port",
                            LocalDate.now().plusDays(2), LocalDate.now().plusDays(5), null, false,
                            OrderStatus.PREPARED, supplier2, customer),
                    new Order("RNR7707143", "Cacao", "Cocoa Beans", "Medium", "Leipzig Port",
                            LocalDate.now(), LocalDate.now().plusDays(12), "DDS653323", true,
                            OrderStatus.PASSED_CUSTOMS, supplier2, customer),
                    new Order("RNR4804344", "Rubber", "Latex", "Large", "Cologne Port",
                            LocalDate.now(), LocalDate.now().plusDays(7), null, false,
                            OrderStatus.IN_HARBOUR, supplier1, customer),
                    new Order("RNR4807155", "Palm Oil", "Crude Palm Oil", "Small", "Munich Port",
                            LocalDate.now().plusDays(2), LocalDate.now().plusDays(15), "DDS675324", true,
                            OrderStatus.SHIPPED, supplier1, customer),
                    new Order("RNR4807146", "Coffee", "Arabica Beans", "Large", "Frankfurt Port",
                            LocalDate.now(), LocalDate.now().plusDays(6), "DDS854325", false,
                            OrderStatus.PREPARED, supplier2, customer),
                    new Order("RNR6537165", "Beef", "Ground Beef", "Medium", "Stuttgart Port",
                            LocalDate.now(), LocalDate.now().plusDays(11), "DDS667326", true,
                            OrderStatus.PASSED_CUSTOMS, supplier1, customer),
                    new Order("RNR3277448", "Soja", "Soy Oil", "Small", "Düsseldorf Port",
                            LocalDate.now(), LocalDate.now().plusDays(14), null, false,
                            OrderStatus.IN_HARBOUR, supplier2, customer),
                    new Order("RNR4907149", "Wood", "Wood Chips", "Large", "Hamburg Port",
                            LocalDate.now(), LocalDate.now().plusDays(9), "DDS676327", true,
                            OrderStatus.SHIPPED, supplier1, customer)
            );

            orderRepository.saveAll(orders);

            System.out.println("Dummy data loaded into the database.");
        };


    }
}
