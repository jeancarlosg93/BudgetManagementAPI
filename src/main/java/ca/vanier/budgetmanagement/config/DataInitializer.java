package ca.vanier.budgetmanagement.config;

import ca.vanier.budgetmanagement.entities.Category;
import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.services.CategoryService;
import ca.vanier.budgetmanagement.services.IncomeService;
import ca.vanier.budgetmanagement.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

import static ca.vanier.budgetmanagement.entities.IncomeType.BONUS;
import static ca.vanier.budgetmanagement.entities.IncomeType.SALARY;

@Configuration
public class DataInitializer {

    UserService userService;
    CategoryService categoryService;
    IncomeService incomeService;

    public DataInitializer(UserService userService, CategoryService categoryService, IncomeService incomeService) {
        this.incomeService = incomeService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Bean
    CommandLineRunner InitUsers(UserService userService) {
        return args -> {
            User user1 = CreateUser("admin", "admin", "ADMIN", "admin", "admin", "admin@budgetmanagement.vanier.ca", "999-999-99999");
            User user2 = CreateUser("Paul", "1234567489", "USER", "Paul", "Smith", "paul.smith@example.com", "123-456-7891");
            User user3 = CreateUser("Jacques", "1234567489", "ADMIN", "Jacques", "Brown", "jacques.brown@example.com", "123-456-7892");


            userService.saveAll(List.of(user1, user2, user3));


            Income income1 = CreateIncome(5000.00, "Monthly salary", user1, LocalDate.now());
            income1.setType(SALARY);
            Income income2 = CreateIncome(3000.00, "Year's end bonus", user2, LocalDate.now());
            income2.setType(BONUS);

            incomeService.save(income1);
            incomeService.save(income2);
        };

    }

    @Bean
    CommandLineRunner InitCategories(CategoryService categoryService) {
        return args -> {
            Category category1 = CreateCategory("Rent");
            Category category2 = CreateCategory("Food");
            Category category3 = CreateCategory("Entertainment");

            categoryService.saveAll(List.of(category1, category2, category3));
        };

    }

    private User CreateUser(String username, String password, String role, String firstName, String lastName,
                            String email, String phone) {
        return new User(username, password, role, firstName, lastName, email, phone);
    }

    private Category CreateCategory(String name) {
        return new Category(name);
    }

    private Income CreateIncome(double amount, String description, User user, LocalDate date) {
        return new Income(amount, description, user, date);

    }
}