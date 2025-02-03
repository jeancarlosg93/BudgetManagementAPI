package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.entities.Budget;
import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.repositories.BudgetRepository;
import ca.vanier.budgetmanagement.services.BudgetService;
import ca.vanier.budgetmanagement.services.ExpenseService;
import ca.vanier.budgetmanagement.services.UserService;
import ca.vanier.budgetmanagement.services.ExpenseCategoryService;
import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.validators.ExpenseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor

@Service
public class BudgetServiceImpl implements BudgetService {

    final private BudgetRepository budgetRepository;
    final private UserService userService;
    final private ExpenseCategoryService categoryService;
    final private ExpenseService expenseService;


    // Save a budget
    @Transactional
    @Override
    public Budget save(Budget budget) {
        GlobalLogger.info(BudgetServiceImpl.class, "Saving budget: {}", budget);
        // If the budget has a user, find the user by id and set it
        if (budget.getUser() != null) {
            User user = userService.findById(budget.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            budget.setUser(user);
        }
        // If the budget has a category, find the category by id and set it
        if (budget.getCategory() != null) {
            ExpenseCategory category = categoryService.findById(budget.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            budget.setCategory(category);
        }
        // Save the budget
        Budget savedBudget = budgetRepository.save(budget);
        GlobalLogger.info(BudgetServiceImpl.class, "Budget saved successfully with id: {}", savedBudget.getId());
        return savedBudget;
    }

    @Override
    public List<Budget> findAll() {
        GlobalLogger.info(BudgetServiceImpl.class, "Fetching all budgets");
        List<Budget> budgets = budgetRepository.findAll();
        budgets.forEach(this::calculateBudgetStatus);
        return budgets;
    }

    @Override
    public Optional<Budget> findById(Long id) {
        Optional<Budget> budgetOpt = budgetRepository.findById(id);
        budgetOpt.ifPresent(this::calculateBudgetStatus);
        return budgetOpt;
    }

    @Transactional
    @Override
    public void deleteBudget(Long id) {
        GlobalLogger.info(BudgetServiceImpl.class, "Deleting budget with id: {}", id);

        Budget budget = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        budgetRepository.deleteById(id);
        GlobalLogger.info(BudgetServiceImpl.class, "Budget deleted successfully with id: {}", id);
    }

    @Transactional
    @Override
    public Budget updateExistingBudget(Long id, Budget budgetDetails) {
        GlobalLogger.info(BudgetServiceImpl.class, "Updating budget with id: {}", id);

        Budget existingBudget = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        existingBudget.setAmount(budgetDetails.getAmount());
        existingBudget.setName(budgetDetails.getName());
        existingBudget.setDescription(budgetDetails.getDescription());
        existingBudget.setStartDate(budgetDetails.getStartDate());
        existingBudget.setEndDate(budgetDetails.getEndDate());

        if (budgetDetails.getCategory() != null) {
            ExpenseCategory category = categoryService.findById(budgetDetails.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            existingBudget.setCategory(category);
        }

        Budget updatedBudget = budgetRepository.save(existingBudget);
        GlobalLogger.info(BudgetServiceImpl.class, "Budget updated successfully: {}", updatedBudget);
        return updatedBudget;
    }


    public List<Budget> find(Long userId) {
        GlobalLogger.info(BudgetServiceImpl.class, "Finding budgets for user with id: {}", userId);
        try {
            User user = userService.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
            List<Budget> budgets = budgetRepository.findAll().stream()
                    .filter(budget -> budget.getUser().getId().equals(user.getId()))
                    .toList();
            budgets.forEach(this::calculateBudgetStatus);
            return budgets;
        } catch (Exception e) {
            GlobalLogger.warn(BudgetServiceImpl.class, "Failed to find budgets for user with id {}: {}", userId,
                    e.getMessage());
            throw e;
        }

    }

    public List<Budget> find(Long userId, Long categoryId) {
        GlobalLogger.info(BudgetServiceImpl.class, "Finding budgets for user with id: {} and category with id: {}",
                userId, categoryId);
        try {
            List<Budget> budgets = find(userId).stream()
                    .filter(budget -> budget.getCategory().getId().equals(categoryId))
                    .toList();
            budgets.forEach(this::calculateBudgetStatus);
            return budgets;
        } catch (Exception e) {
            GlobalLogger.warn(BudgetServiceImpl.class,
                    "Failed to find budgets for user with id {} and category with id {}: {}", userId, categoryId,
                    e.getMessage());
            throw e;
        }
    }

    public List<Budget> find(Long userId, LocalDate startDate, LocalDate endDate) {
        GlobalLogger.info(BudgetServiceImpl.class, "Finding budgets for user with id: {} and date range: {} to {}",
                userId, startDate, endDate);
        try {
            List<Budget> budgets = find(userId).stream()
                    .filter(budget ->
                            (budget.getStartDate().isAfter(startDate) || budget.getStartDate().isEqual(startDate)) &&
                                    (budget.getEndDate().isBefore(endDate) || budget.getEndDate().isEqual(
                                            endDate))).toList();
            budgets.forEach(this::calculateBudgetStatus);
            return budgets;
        } catch (Exception e) {
            GlobalLogger.warn(BudgetServiceImpl.class,
                    "Failed to find budgets for user with id {} and date range: {} to {}: {}", userId, startDate,
                    endDate, e.getMessage());
            throw e;
        }
    }

    public List<Budget> find(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {
        GlobalLogger.info(BudgetServiceImpl.class,
                "Finding budgets for user with id: {}, category with id: {}, and date range: {} to {}", userId,
                categoryId, startDate, endDate);
        try {
            List<Budget> budgets = find(userId, categoryId).stream()
                    .filter(budget -> !budget.getStartDate().isBefore(startDate) && !budget.getEndDate().isAfter(
                            endDate))
                    .toList();
            budgets.forEach(this::calculateBudgetStatus);
            return budgets;
        } catch (Exception e) {
            GlobalLogger.warn(BudgetServiceImpl.class,
                    "Failed to find budgets for user with id {}, category with id {}, and date range: {} to {}: {}",
                    userId, categoryId, startDate, endDate, e.getMessage());
            throw e;
        }
    }

    public List<Budget> findWithFilters(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {

        boolean hasCategoryId = categoryId != null;
        boolean hasDateRange = startDate != null && endDate != null;
        if (hasCategoryId) {
            ExpenseValidator.validateExpenseCategory(categoryId, userId);

        }
        try {
            if (hasCategoryId && hasDateRange) {
                return find(userId, categoryId, startDate, endDate);
            } else if (hasDateRange) {
                return find(userId, startDate, endDate);
            } else if (hasCategoryId) {
                return find(userId, categoryId);
            } else {
                return find(userId);
            }
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to find expenses: {}", e.getMessage());
            throw e;
        }
    }


    // Calculate the budget status
    // This method calculates the actual expenses for a budget
    // by summing the expenses for the budget's user, category, and month
    // and setting the budget's actual expenses and remaining amount
    public void calculateBudgetStatus(Budget budget) {

        List<Expense> expenses = expenseService.find(
                budget.getUser().getId(),
                budget.getCategory().getId()
        );

        // In BudgetServiceImpl.calculateBudgetStatus()
        GlobalLogger.info(BudgetServiceImpl.class, "Total expenses fetched: {}", expenses.size());
        int count = 0;
        for (Expense expense : expenses) {
            if (expense.getDate() != null &&
                    !expense.getDate().isBefore(budget.getStartDate()) &&
                    !expense.getDate().isAfter(budget.getEndDate())) {
                count++;
            }
        }
        GlobalLogger.info(BudgetServiceImpl.class, "Expenses within date range: {}", count);

        // Then filter the dates manually to match the budget's date range
        double totalExpenses = 0.0;
        for (Expense expense : expenses) {
            if (expense.getDate() != null &&
                    !expense.getDate().isBefore(budget.getStartDate()) &&
                    !expense.getDate().isAfter(budget.getEndDate())) {
                double amount = expense.getAmount();
                totalExpenses += amount;
            }
        }

        budget.setActualExpenses(totalExpenses);

        GlobalLogger.info(BudgetServiceImpl.class,
                "Calculated budget status for budget {}: actualExpenses={}, remainingAmount={}",
                budget.getId(),
                totalExpenses,
                budget.getRemainingAmount());
    }


}