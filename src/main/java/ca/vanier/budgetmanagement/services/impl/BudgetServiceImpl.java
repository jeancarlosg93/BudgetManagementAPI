package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.entities.Budget;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.repositories.BudgetRepository;
import ca.vanier.budgetmanagement.services.BudgetService;
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
    public Optional<Budget> findById(Long id) {
        GlobalLogger.info(BudgetServiceImpl.class, "Finding budget by id: {}", id);
        return budgetRepository.findById(id);
    }

    @Override
    public List<Budget> findByUserId(Long userId) {
        GlobalLogger.info(BudgetServiceImpl.class, "Finding budgets by user id: {}", userId);

        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return budgetRepository.findAll().stream()
                .filter(budget -> budget.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId) {
        GlobalLogger.info(BudgetServiceImpl.class, "Finding budgets by user id {} and category id: {}", userId, categoryId);

        return findByUserId(userId).stream()
                .filter(budget -> budget.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }
}