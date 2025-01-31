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
}