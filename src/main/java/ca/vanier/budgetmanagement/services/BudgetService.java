package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Budget;

import java.util.List;
import java.util.Optional;

public interface BudgetService {
    Budget save(Budget budget);

    List<Budget> findAll();

    void deleteBudget(Long id);

    Budget updateExistingBudget(Long id, Budget budgetDetails);

    Optional<Budget> findById(Long id);

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);
}