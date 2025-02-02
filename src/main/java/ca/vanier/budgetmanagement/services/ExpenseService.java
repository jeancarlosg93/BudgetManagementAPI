package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseService {

    Expense save(Expense expense);

    List<Expense> findAll();

    void deleteExpense(Long id);

    Expense updateExistingExpense(Long id, Expense expenseDetails);

    Optional<Expense> findById(Long id);

    List<Expense> find(Long userid);

    List<Expense> find(Long userid, LocalDate startDate, LocalDate endDate);

    List<Expense> find(Long userid, LocalDate startDate, LocalDate endDate, Long category);

    List<Expense> find(Long userid, Long category);

    List<Expense> findWithFilters(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);

}