package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.ExpenseCategory;

import java.util.List;
import java.util.Optional;

public interface ExpenseCategoryService {

    ExpenseCategory save(ExpenseCategory expenseCategory);

    Optional<ExpenseCategory> findById(Long id);

    ExpenseCategory updateExistingExpenseCategory(Long id, ExpenseCategory expenseCategoryDetails);

    List<ExpenseCategory> findAll();

    void deleteExpenseCategory(Long id);

}