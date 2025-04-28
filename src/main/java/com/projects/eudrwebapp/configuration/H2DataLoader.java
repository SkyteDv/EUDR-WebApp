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
            // Insert default suppliers with osapiensID in the format OS12342
            userRepository.save(new User("GreenTechSupply", "1", "SUPPLIER", "SUP001"));
            userRepository.save(new User("AlphaMaterials", "1", "SUPPLIER", "SUP002"));
            userRepository.save(new User("BlueSkyLogistics", "1", "SUPPLIER", "SUP003"));
            userRepository.save(new User("SolarisManufacturing", "1", "SUPPLIER", "SUP004"));
            userRepository.save(new User("TechnoWarehouse", "1", "SUPPLIER", "SUP005"));


            userRepository.save(new User("C1", "1", "CUSTOMER", "CUST001"));
            userRepository.save(new User("C2", "1", "CUSTOMER", "CUST002"));
            userRepository.save(new User("C3", "1", "CUSTOMER", "CUST003"));
            userRepository.save(new User("C4", "1", "CUSTOMER", "CUST004"));

            User customer = new User("C5", "1", "CUSTOMER", "CUST005");
            userRepository.save(customer);

            // Assign the suppliers to the customer

            userRepository.save(customer);



            System.out.println("Dummy data loaded into the database.");
        };
    }
}
