package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Budget;
import ca.vanier.budgetmanagement.entities.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetService {
    Budget save(Budget budget);

    List<Budget> findAll();

    void deleteBudget(Long id);

    Budget updateExistingBudget(Long id, Budget budgetDetails);

    Optional<Budget> findById(Long id);

    List<Budget> find(Long userId);

    List<Budget> find(Long userId, Long categoryId);

    List<Budget> find(Long userId, LocalDate startDate, LocalDate endDate);

    List<Budget> find(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);

    List<Budget> findWithFilters(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);

    void calculateBudgetStatus(Budget budget);
}