package ca.vanier.budgetmanagement.config;

import ca.vanier.budgetmanagement.entities.*;
import ca.vanier.budgetmanagement.services.IncomeService;
import ca.vanier.budgetmanagement.services.ExpenseCategoryService;
import ca.vanier.budgetmanagement.services.ReportService;
import ca.vanier.budgetmanagement.services.ExpenseService;
import ca.vanier.budgetmanagement.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

import static ca.vanier.budgetmanagement.entities.IncomeType.*;

@Configuration
public class DataInitializer {

    UserService userService;
    IncomeService incomeService;
    ExpenseCategoryService expenseCategoryService;
    ReportService reportService;
    ExpenseService expenseService;

    public DataInitializer(UserService userService, ExpenseCategoryService expenseCategoryService,
            IncomeService incomeService, ReportService reportService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseCategoryService = expenseCategoryService;
        this.incomeService = incomeService;
        this.reportService = reportService;
        this.expenseService = expenseService;
    }

    @Bean
    CommandLineRunner Init(UserService userService) {
        return args -> {
            User user1 = CreateUser("admin", "admin", "ADMIN", "admin", "admin", "admin@budgetmanagement.vanier.ca",
                    "999-999-99999");
            User user2 = CreateUser("Paul", "1234567489", "USER", "Paul", "Smith", "paul.smith@example.com",
                    "123-456-7891");
            User user3 = CreateUser("Jacques", "1234567489", "ADMIN", "Jacques", "Brown", "jacques.brown@example.com",
                    "123-456-7892");

            userService.saveAll(List.of(user1, user2, user3));


            List<Income> user1Incomes = List.of(
                    CreateIncome(5000.00, "Monthly salary", user1, LocalDate.now().minusDays(3), SALARY),
                    CreateIncome(2000.00, "Investment returns", user1, LocalDate.now().minusDays(15), INTEREST),
                    CreateIncome(3000.00, "Consulting work", user1, LocalDate.now().minusDays(45), OTHER),
                    CreateIncome(500.00, "Dividend payment", user1, LocalDate.now().minusDays(20), DIVIDEND)
            );

            // User 2 Incomes
            List<Income> user2Incomes = List.of(
                    CreateIncome(4500.00, "Monthly salary", user2, LocalDate.now().minusDays(5), SALARY),
                    CreateIncome(3000.00, "Year-end bonus", user2, LocalDate.now().minusDays(2), BONUS),
                    CreateIncome(1000.00, "Rental income", user2, LocalDate.now().minusDays(10), RENT)
            );

            // User 3 Incomes
            List<Income> user3Incomes = List.of(
                    CreateIncome(6000.00, "Monthly salary", user3, LocalDate.now().minusDays(7), SALARY),
                    CreateIncome(4000.00, "Performance bonus", user3, LocalDate.now().minusDays(25), BONUS),
                    CreateIncome(2500.00, "Stock dividends", user3, LocalDate.now().minusDays(30), DIVIDEND)
            );

            user1Incomes.forEach(income -> incomeService.save(income));
            user2Incomes.forEach(income -> incomeService.save(income));
            user3Incomes.forEach(income -> incomeService.save(income));

            CreateReport(user1, LocalDate.now().minusMonths(1), LocalDate.now());
            CreateReport(user2, LocalDate.now().minusMonths(1), LocalDate.now());
            CreateReport(user3, LocalDate.now().minusMonths(1), LocalDate.now());

            ExpenseCategory category1 = CreateExpenseCategory("Rent", user1);
            ExpenseCategory category2 = CreateExpenseCategory("Food",
                    user2);
            ExpenseCategory category3 = CreateExpenseCategory("Entertainment",
                    user3);

            List<ExpenseCategory> expenseCategories = List.of(category1, category2,
                    category3);
            expenseCategories.forEach(expenseCategory -> expenseCategoryService.save(expenseCategory));

            Expense expense1 = CreateExpense(2300.30, "Monthly rent", userService.findById((long) 1).get(),
                    LocalDate.now(), category1);
            Expense expense2 = CreateExpense(343.23, "Fine dining", userService.findById((long) 2).get(),
                    LocalDate.now(), category2);
            Expense expense3 = CreateExpense(40.25, "Concert ticket", userService.findById((long) 3).get(),
                    LocalDate.now(), category3);

            List<Expense> expenses = List.of(expense1, expense2, expense3);
            expenses.forEach(expense -> expenseService.save(expense));

        };

    }

    private User CreateUser(String username, String password, String role, String firstName, String lastName,
            String email, String phone) {
        return new User(username, password, role, firstName, lastName, email, phone);
    }

    private ExpenseCategory CreateExpenseCategory(String name, User user) {
        return new ExpenseCategory(name, user);
    }

    private Income CreateIncome(double amount, String description, User user, LocalDate date, IncomeType type) {
        return new Income(amount, description, user, date, type);
    }

    private Expense CreateExpense(double amount, String description, User user, LocalDate date,
            ExpenseCategory category) {
        return new Expense(amount, description, user, date, category);
    }

    private void CreateReport(User user, LocalDate startDate, LocalDate endDate) {
        reportService.createReport(user.getId(), startDate, endDate);
    }
}