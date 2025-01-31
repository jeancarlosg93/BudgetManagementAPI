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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseCategoryService categoryService;

    @Autowired
    private ExpenseService expenseService;


    @Transactional
    @Override
    public Budget save(Budget budget) {
        GlobalLogger.info(BudgetServiceImpl.class, "Saving budget: {}", budget);

        if (budget.getUser() != null) {
            User user = userService.findById(budget.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            budget.setUser(user);
        }

        if (budget.getCategory() != null) {
            ExpenseCategory category = categoryService.findById(budget.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            budget.setCategory(category);
        }

        Budget savedBudget = budgetRepository.save(budget);
        GlobalLogger.info(BudgetServiceImpl.class, "Budget saved successfully with id: {}", savedBudget.getId());
        return savedBudget;
    }

    @Override
    public List<Budget> findAll() {
        GlobalLogger.info(BudgetServiceImpl.class, "Fetching all budgets");
        return budgetRepository.findAll();
    }

    @Override
    public Optional<Budget> findById(Long id) {
        Optional<Budget> budgetOpt = budgetRepository.findById(id);
        budgetOpt.ifPresent(this::calculateBudgetStatus);
        return budgetOpt;
    }

    @Override
    public List<Budget> findByUserId(Long userId) {
        List<Budget> budgets = budgetRepository.findAll().stream()
                .filter(budget -> budget.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        budgets.forEach(this::calculateBudgetStatus);
        return budgets;
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


    @Override
    public List<Budget> findByUserIdAndMonthAndYear(Long userId, int month, int year) {
        List<Budget> budgets = budgetRepository.findAll().stream()
                .filter(budget ->
                        budget.getUser().getId().equals(userId) &&
                                budget.getStartDate().getMonthValue() == month &&
                                budget.getStartDate().getYear() == year)
                .collect(Collectors.toList());
        budgets.forEach(this::calculateBudgetStatus);
        return budgets;
    }


    @Override
    public List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId) {
        GlobalLogger.info(BudgetServiceImpl.class, "Finding budgets by user id {} and category id: {}", userId, categoryId);

        return findByUserId(userId).stream()
                .filter(budget -> budget.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    private void calculateBudgetStatus(Budget budget) {
        List<Expense> expenses = expenseService.findByUserIdAndCategoryIdAndMonthAndYear(
                budget.getUser().getId(),
                budget.getCategory().getId(),
                budget.getStartDate().getMonthValue(),
                budget.getStartDate().getYear()
        );

        double totalExpenses = expenses.stream()
                .filter(expense ->
                        !expense.getDate().isBefore(budget.getStartDate()) &&
                                !expense.getDate().isAfter(budget.getEndDate())
                )
                .mapToDouble(Expense::getAmount)
                .sum();


        budget.setActualExpenses(totalExpenses);

        GlobalLogger.info(BudgetServiceImpl.class,
                "Calculated budget status for budget {}: actualExpenses={}, remainingAmount={}",
                budget.getId(),
                totalExpenses,
                budget.getRemainingAmount());
    }


}