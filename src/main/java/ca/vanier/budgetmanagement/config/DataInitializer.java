package ca.vanier.budgetmanagement.config;

import ca.vanier.budgetmanagement.entities.*;
import ca.vanier.budgetmanagement.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

import static ca.vanier.budgetmanagement.entities.IncomeType.*;

@Configuration
public class DataInitializer {

        private final UserService userService;
        private final IncomeService incomeService;
        private final ExpenseCategoryService expenseCategoryService;
        private final ReportService reportService;
        private final ExpenseService expenseService;
        private final BudgetService budgetService;

        public DataInitializer(UserService userService,
                        ExpenseCategoryService expenseCategoryService,
                        IncomeService incomeService,
                        ReportService reportService,
                        ExpenseService expenseService,
                        BudgetService budgetService) {
                this.userService = userService;
                this.expenseCategoryService = expenseCategoryService;
                this.incomeService = incomeService;
                this.reportService = reportService;
                this.expenseService = expenseService;
                this.budgetService = budgetService;
        }

        @Bean
        CommandLineRunner init() {
                return args -> {
                        List<User> users = addUsers();
                        addIncomes(users);
                        List<ExpenseCategory> expenseCategories = addExpenseCategories(users);
                        addExpenses(users, expenseCategories);
                        addBudgets(users, expenseCategories);
                        addReports(users);
                };
        }

        private List<User> addUsers() {
                List<User> users = List.of(
                                createUser("admin", "admin", "ADMIN", "admin", "admin",
                                                "admin@budgetmanagement.vanier.ca", "999-999-99999"),
                                createUser("Paul", "1234567489", "USER", "Paul", "Smith", "paul.smith@example.com",
                                                "123-456-7891"),
                                createUser("Jacques", "1234567489", "ADMIN", "Jacques", "Brown",
                                                "jacques.brown@example.com", "123-456-7892"));
                userService.saveAll(users);
                return users;
        }

        private void addIncomes(List<User> users) {
                List<List<Income>> incomes = List.of(
                                List.of(
                                                createIncome(5000.00, "Monthly salary", users.get(0),
                                                                LocalDate.now().minusDays(3), SALARY),
                                                createIncome(2000.00, "Investment returns", users.get(0),
                                                                LocalDate.now().minusDays(15), INTEREST),
                                                createIncome(3000.00, "Consulting work", users.get(0),
                                                                LocalDate.now().minusDays(45), OTHER),
                                                createIncome(500.00, "Dividend payment", users.get(0),
                                                                LocalDate.now().minusDays(20), DIVIDEND)),
                                List.of(
                                                createIncome(4500.00, "Monthly salary", users.get(1),
                                                                LocalDate.now().minusDays(5), SALARY),
                                                createIncome(3000.00, "Year-end bonus", users.get(1),
                                                                LocalDate.now().minusDays(2), BONUS),
                                                createIncome(1000.00, "Rental income", users.get(1),
                                                                LocalDate.now().minusDays(10), RENT)),
                                List.of(
                                                createIncome(6000.00, "Monthly salary", users.get(2),
                                                                LocalDate.now().minusDays(7), SALARY),
                                                createIncome(4000.00, "Performance bonus", users.get(2),
                                                                LocalDate.now().minusDays(25), BONUS),
                                                createIncome(2500.00, "Stock dividends", users.get(2),
                                                                LocalDate.now().minusDays(30), DIVIDEND)));
                incomes.forEach(list -> list.forEach(income -> incomeService.save(income)));
        }

        private List<ExpenseCategory> addExpenseCategories(List<User> users) {
                List<ExpenseCategory> categories = List.of(
                                createExpenseCategory("Rent", users.get(0)),
                                createExpenseCategory("Food", users.get(1)),
                                createExpenseCategory("Entertainment", users.get(2)));
                categories.forEach(expenseCategoryService::save);
                return categories;
        }

        private void addExpenses(List<User> users, List<ExpenseCategory> categories) {
                List<Expense> expenses = List.of(
                                createExpense(2300.30, "Monthly rent", users.get(0), LocalDate.now().minusDays(5),
                                                categories.get(0)),
                                createExpense(343.23, "Fine dining", users.get(1), LocalDate.now().minusDays(2),
                                                categories.get(1)),
                                createExpense(40.25, "Concert ticket", users.get(2), LocalDate.now().minusDays(3),
                                                categories.get(2)));
                expenses.forEach(expenseService::save);
        }

        private void addBudgets(List<User> users, List<ExpenseCategory> categories) {
                List<Budget> budgets = List.of(
                                createBudget(3000.00, "Monthly Housing Budget", "Budget for rent and utilities",
                                                users.get(0), categories.get(0)),
                                createBudget(500.00, "Food Budget", "Monthly food expenses", users.get(1),
                                                categories.get(1)),
                                createBudget(200.00, "Entertainment Budget", "Monthly entertainment expenses",
                                                users.get(2), categories.get(2)));
                budgets.forEach(budgetService::save);
        }

        private void addReports(List<User> users) {
                users.forEach(user -> createReport(user, LocalDate.now().minusMonths(1), LocalDate.now()));
        }

        private User createUser(String username, String password, String role, String firstName, String lastName,
                        String email, String phone) {
                return new User(username, password, role, firstName, lastName, email, phone);
        }

        private ExpenseCategory createExpenseCategory(String name, User user) {
                return new ExpenseCategory(name, user);
        }

        private Income createIncome(double amount, String description, User user, LocalDate date, IncomeType type) {
                return new Income(amount, description, user, date, type);
        }

        private Expense createExpense(double amount, String description, User user, LocalDate date,
                        ExpenseCategory category) {
                return new Expense(amount, description, user, date, category);
        }

        private void createReport(User user, LocalDate startDate, LocalDate endDate) {
                reportService.createReport(user.getId(), startDate, endDate);
        }

        private Budget createBudget(double amount, String name, String description, User user,
                        ExpenseCategory category) {
                return new Budget(amount, name, description, user,
                                LocalDate.now().withDayOfMonth(1),
                                LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()),
                                category);
        }
}