package ca.vanier.budgetmanagement.config;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.Report;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.services.IncomeService;
import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.services.ExpenseCategoryService;
import ca.vanier.budgetmanagement.services.ReportService;
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
    IncomeService incomeService;
    ExpenseCategoryService expenseCategoryService;
    ReportService reportService;

    public DataInitializer(UserService userService, ExpenseCategoryService expenseCategoryService, IncomeService incomeService, ReportService reportService) {
        this.userService = userService;
        this.expenseCategoryService = expenseCategoryService;
        this.incomeService = incomeService;
        this.reportService = reportService;
    }

    @Bean
    CommandLineRunner InitUsers(UserService userService) {
        return args -> {
            User user1 = CreateUser("admin", "admin", "ADMIN", "admin", "admin", "admin@budgetmanagement.vanier.ca", "999-999-99999");
            User user2 = CreateUser("Paul", "1234567489", "USER", "Paul", "Smith", "paul.smith@example.com", "123-456-7891");
            User user3 = CreateUser("Jacques", "1234567489", "ADMIN", "Jacques", "Brown", "jacques.brown@example.com", "123-456-7892");

            userService.saveAll(List.of(user1, user2, user3));

            Income income1 = CreateIncome(5000.00, "Monthly salary", user1, LocalDate.now().minusDays(3));
            income1.setType(SALARY);
            Income income2 = CreateIncome(3000.00, "Year's end bonus", user2, LocalDate.now().minusDays(2));
            income2.setType(BONUS);

            incomeService.save(income1);
            incomeService.save(income2);

            CreateReport(user1, LocalDate.now().minusMonths(1), LocalDate.now());
            CreateReport(user2, LocalDate.now().minusMonths(1), LocalDate.now());
            CreateReport(user3, LocalDate.now().minusMonths(1), LocalDate.now());
        };

    }

    @Bean
    CommandLineRunner InitExpenseCategories(ExpenseCategoryService categoryService) {

        return args -> {
            ExpenseCategory category1 = CreateExpenseCategory("Rent", (long) 1);
            ExpenseCategory category2 = CreateExpenseCategory("Food",
                    (long) 2);
            ExpenseCategory category3 = CreateExpenseCategory("Entertainment",
                    (long) 3);

            List<ExpenseCategory> expenseCategories = List.of(category1, category2,
                    category3);
            expenseCategories.forEach(expenseCategory -> expenseCategoryService.save(expenseCategory));
        };

    }

    private User CreateUser(String username, String password, String role, String firstName, String lastName,
                            String email, String phone) {
        return new User(username, password, role, firstName, lastName, email, phone);
    }

    private ExpenseCategory CreateExpenseCategory(String name, Long userId) {
        return new ExpenseCategory(name, userId);
    }

    private Income CreateIncome(double amount, String description, User user, LocalDate date) {
        return new Income(amount, description, user, date);

    }

    private void CreateReport(User user, LocalDate startDate, LocalDate endDate) {
        reportService.createReport(user.getId(), startDate, endDate);
    }
}