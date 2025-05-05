package com.projects.eudrwebapp.configuration;

import com.projects.eudrwebapp.model.Order;
import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.OrderRepository;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2DataLoader {

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, OrderRepository orderRepository) {
        return args -> {
            // Only load dummy data if the user table is empty
            if (userRepository.count() == 0) {
                // Insert default suppliers
                userRepository.save(new User("S1", "1", "SUPPLIER", "SUP001"));
                userRepository.save(new User("S2", "1", "SUPPLIER", "SUP002"));
                userRepository.save(new User("S3", "1", "SUPPLIER", "SUP003"));
                userRepository.save(new User("S4", "1", "SUPPLIER", "SUP004"));
                userRepository.save(new User("S5", "1", "SUPPLIER", "SUP005"));

                // Insert default customers
                userRepository.save(new User("C1", "1", "CUSTOMER", "CUST001"));
                userRepository.save(new User("C2", "1", "CUSTOMER", "CUST002"));
                userRepository.save(new User("C3", "1", "CUSTOMER", "CUST003"));
                userRepository.save(new User("C4", "1", "CUSTOMER", "CUST004"));

                User customer = new User("C5", "1", "CUSTOMER", "CUST005");
                userRepository.save(customer);

                System.out.println("Dummy data loaded into the database.");
            } else {
                System.out.println("Data already exists. Skipping initial load.");
            }
        };
    }
}
