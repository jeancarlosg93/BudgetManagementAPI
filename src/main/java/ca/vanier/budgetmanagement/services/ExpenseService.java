package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.entities.ExpenseCategory;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {

    Expense save(Expense expense);

    List<Expense> findAll();

    void deleteExpense(Long id);

    Expense updateExistingExpense(Long id, Expense expenseDetails);

    Optional<Expense> findById(Long id);

    List<Expense> findByUserId(Long userId);

    List<Expense> findByUserIdAndMonth(Long userId, int month);

    List<Expense> findByUserIdAndYear(Long userId, int year);

    List<Expense> findByUserIdAndMonthAndYear(Long userId, int month, int year);

    List<Expense> findByUserIdAndMonthAndYearAndCategoryId(Long userId, int month, int year, Long categoryId);

    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Expense> findByUserIdAndYearAndCategoryId(Long userId, int year, Long categoryId);

    List<Expense> findByUserIdAndCategoryIdAndMonth(Long userId, Long categoryId, int month);

    List<Expense> findByUserIdAndCategoryIdAndYear(Long userId, Long categoryId, int year);

    List<Expense> findByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, int month, int year);

}