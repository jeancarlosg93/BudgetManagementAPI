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
            User user1 = CreateUser("Jean", "1234567489");
            User user2 = CreateUser("Paul", "1234567489");
            User user3 = CreateUser("Jacques", "1234567489");
            User user4 = CreateUser("Marie", "1234567489");
            User user5 = CreateUser("Sophie", "1234567489");
            User user6 = CreateUser("Louise", "1234567489");
            User user7 = CreateUser("Pierre", "1234567489");
            User user8 = CreateUser("Luc", "1234567489");
            User user9 = CreateUser("François", "1234567489");
            User user0 = CreateUser("Michel", "1234567489");


            userService.saveAll(List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9, user0));
        };
    }

    private User CreateUser(String name, String password) {
        return new User(name, password);
    }
}