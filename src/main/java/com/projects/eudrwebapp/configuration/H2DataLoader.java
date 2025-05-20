package com.projects.eudrwebapp.configuration;

import com.projects.eudrwebapp.model.Country;
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
                userRepository.save(new User("S1", "1", "SUPPLIER", "SUP001", Country.UNITED_STATES));
                userRepository.save(new User("S2", "1", "SUPPLIER", "SUP002", Country.CANADA));
                userRepository.save(new User("S3", "1", "SUPPLIER", "SUP003", Country.MEXICO));
                userRepository.save(new User("S4", "1", "SUPPLIER", "SUP004", Country.BRAZIL));
                userRepository.save(new User("S5", "1", "SUPPLIER", "SUP005", Country.ARGENTINA));
                userRepository.save(new User("S6", "1", "SUPPLIER", "SUP006", Country.COLOMBIA));
                userRepository.save(new User("S7", "1", "SUPPLIER", "SUP007", Country.CHINA));
                userRepository.save(new User("S8", "1", "SUPPLIER", "SUP008", Country.INDIA));
                userRepository.save(new User("S9", "1", "SUPPLIER", "SUP009", Country.JAPAN));
                userRepository.save(new User("S10", "1", "SUPPLIER", "SUP010", Country.KOREA_SOUTH));
                userRepository.save(new User("S11", "1", "SUPPLIER", "SUP011", Country.INDONESIA));
                userRepository.save(new User("S12", "1", "SUPPLIER", "SUP012", Country.VIETNAM));
                userRepository.save(new User("S13", "1", "SUPPLIER", "SUP013", Country.THAILAND));
                userRepository.save(new User("S14", "1", "SUPPLIER", "SUP014", Country.AUSTRALIA));
                userRepository.save(new User("S15", "1", "SUPPLIER", "SUP015", Country.NEW_ZEALAND));
                userRepository.save(new User("S16", "1", "SUPPLIER", "SUP016", Country.SOUTH_AFRICA));
                userRepository.save(new User("S17", "1", "SUPPLIER", "SUP017", Country.EGYPT));
                userRepository.save(new User("S18", "1", "SUPPLIER", "SUP018", Country.KENYA));
                userRepository.save(new User("S19", "1", "SUPPLIER", "SUP019", Country.NIGERIA));
                userRepository.save(new User("S20", "1", "SUPPLIER", "SUP020", Country.TURKEY));


                // Insert default customers
                userRepository.save(new User("C1", "1", "CUSTOMER", "CUST001", Country.GERMANY));
                userRepository.save(new User("C2", "1", "CUSTOMER", "CUST002", Country.SPAIN));
                userRepository.save(new User("C3", "1", "CUSTOMER", "CUST003", Country.ITALY));
                userRepository.save(new User("C4", "1", "CUSTOMER", "CUST004", Country.FRANCE));

                User customer = new User("C5", "1", "CUSTOMER", "CUST005", Country.PORTUGAL);
                userRepository.save(customer);

                System.out.println("Dummy data loaded into the database.");
            } else {
                System.out.println("Data already exists. Skipping initial load.");
            }
        };
    }
}
