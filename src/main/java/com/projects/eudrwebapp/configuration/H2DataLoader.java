package com.projects.eudrwebapp.configuration;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class H2DataLoader {

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository) {
        return args -> {
            // Insert default suppliers
            userRepository.save(new User("GreenTechSupply", "password123", "SUPPLIER"));
            userRepository.save(new User("AlphaMaterials", "password123", "SUPPLIER"));
            userRepository.save(new User("BlueSkyLogistics", "password123", "SUPPLIER"));
            userRepository.save(new User("SolarisManufacturing", "password123", "SUPPLIER"));
            userRepository.save(new User("NextGenComponents", "password123", "SUPPLIER"));
            userRepository.save(new User("TechnoWarehouse", "password123", "SUPPLIER"));
            userRepository.save(new User("EcoFlowIndustries", "password123", "SUPPLIER"));
            userRepository.save(new User("PrimeSolutions", "password123", "SUPPLIER"));
            userRepository.save(new User("SwiftPartsDistribution", "password123", "SUPPLIER"));
            userRepository.save(new User("TitanWare", "password123", "SUPPLIER"));
            userRepository.save(new User("QuantumSupplyChain", "password123", "SUPPLIER"));
            userRepository.save(new User("PinnacleResources", "password123", "SUPPLIER"));
            userRepository.save(new User("MaverickIndustries", "password123", "SUPPLIER"));
            userRepository.save(new User("VanguardMaterials", "password123", "SUPPLIER"));
            userRepository.save(new User("NovaTechSupply", "password123", "SUPPLIER"));

            // Insert a customer
            User customer = new User("1", "1", "CUSTOMER");
            userRepository.save(customer);

            System.out.println("Dummy data loaded into the database.");

            // Get the list of suppliers
            User supplier1 = userRepository.findByUsername("GreenTechSupply").orElseThrow();
            User supplier2 = userRepository.findByUsername("AlphaMaterials").orElseThrow();
            User supplier3 = userRepository.findByUsername("BlueSkyLogistics").orElseThrow();

            // Assign the suppliers to the customer
            customer.setSuppliers(Arrays.asList(supplier1, supplier2, supplier3));

            // Save the updated customer with the assigned suppliers
            userRepository.save(customer);

            // Fetch the customer from the database to confirm the suppliers are assigned
            User savedCustomer = userRepository.findByUsername("1").orElseThrow();
            System.out.println("Customer: " + savedCustomer.getUsername());
            savedCustomer.getSuppliers().forEach(supplier -> System.out.println("Assigned Supplier: " + supplier.getUsername()));

            System.out.println("Dummy data loaded and supplier assignment verified.");

        };
    }
}
