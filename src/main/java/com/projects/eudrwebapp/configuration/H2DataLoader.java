package com.projects.eudrwebapp.configuration;

import com.projects.eudrwebapp.model.User;
import com.projects.eudrwebapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            userRepository.save(new User("1", "1", "CUSTOMER"));

            System.out.println("Dummy data loaded into the database.");
        };
    }
}
