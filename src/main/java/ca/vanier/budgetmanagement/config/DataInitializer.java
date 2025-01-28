package ca.vanier.budgetmanagement.config;

import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;

import java.util.List;

@Configuration
public class DataInitializer {

    UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Bean
    CommandLineRunner Init(UserService userService) {
        return args -> {
            User user1 = CreateUser("admin", "admin", "ADMIN", "admin", "admin", "admin@budgetmanagement.vanier.ca", "999-999-99999");
            User user2 = CreateUser("Paul", "1234567489", "USER", "Paul", "Smith", "paul.smith@example.com", "123-456-7891");
            User user3 = CreateUser("Jacques", "1234567489", "ADMIN", "Jacques", "Brown", "jacques.brown@example.com", "123-456-7892");
            User user4 = CreateUser("Marie", "1234567489", "USER", "Marie", "Johnson", "marie.johnson@example.com", "123-456-7893");
            User user5 = CreateUser("Sophie", "1234567489", "USER", "Sophie", "Williams", "sophie.williams@example.com", "123-456-7894");
            User user6 = CreateUser("Louise", "1234567489", "USER", "Louise", "Jones", "louise.jones@example.com", "123-456-7895");
            User user7 = CreateUser("Pierre", "1234567489", "ADMIN", "Pierre", "Garcia", "pierre.garcia@example.com", "123-456-7896");
            User user8 = CreateUser("Luc", "1234567489", "USER", "Luc", "Martinez", "luc.martinez@example.com", "123-456-7897");
            User user9 = CreateUser("François", "1234567489", "USER", "François", "Rodriguez", "francois.rodriguez@example.com", "123-456-7898");
            User user0 = CreateUser("Michel", "1234567489", "USER", "Michel", "Hernandez", "michel.hernandez@example.com", "123-456-7899");

            userService.saveAll(List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9, user0));
        };

    }

    private User CreateUser(String username, String password, String role, String firstName, String lastName, String email, String phone) {
        return new User(username, password, role, firstName, lastName, email, phone);
    }
}