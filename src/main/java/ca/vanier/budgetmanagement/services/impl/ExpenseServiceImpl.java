package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.repositories.ExpenseRepository;
import ca.vanier.budgetmanagement.services.ExpenseService;
import ca.vanier.budgetmanagement.services.UserService;
import ca.vanier.budgetmanagement.services.ExpenseCategoryService;

import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.validators.ExpenseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseCategoryService expenseCategoryService;

    @Transactional
    @Override
    public Expense save(Expense expense) {
        GlobalLogger.info(ExpenseServiceImpl.class, "Saving expense: {}", expense.toString());

        ExpenseValidator.validateExpense(expense);

        if (expense.getUser() != null) {
            try {
                User user = userService.findById(expense.getUser().getId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                user.getExpenses().add(expense);
                expense.setUser(user);
                GlobalLogger.info(ExpenseServiceImpl.class, "Associated expense with user id: {}", user.getId());
            } catch (IllegalArgumentException e) {
                GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to find user for expense: {}", e.getMessage());
                throw e;
            }
        }

        Expense savedExpense = expenseRepository.save(expense);
        GlobalLogger.info(ExpenseServiceImpl.class, "Expense saved successfully with id: {}", savedExpense.getId());
        return savedExpense;
    }

    @Override
    public List<Expense> findAll() {
        GlobalLogger.info(ExpenseServiceImpl.class, "Fetching all expenses");
        return expenseRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteExpense(Long id) {
        GlobalLogger.info(ExpenseServiceImpl.class, "Deleting expense with id: {}", id);

        try {
            Expense expense = findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

            if (expense.getUser() != null) {
                expense.getUser().getExpenses().remove(expense);
                expense.setUser(null);
                GlobalLogger.info(ExpenseServiceImpl.class, "Removed expense association from user");
            }

            expenseRepository.deleteById(id);
            GlobalLogger.info(ExpenseServiceImpl.class, "Expense with id {} deleted successfully", id);
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to delete expense: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    @Override
    public Expense updateExistingExpense(Long id, Expense expenseDetails) {

        ExpenseValidator.validateExpense(expenseDetails);

        GlobalLogger.info(ExpenseServiceImpl.class, "Updating expense with id: {}", id);

        try {
            Expense existingExpense = findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

            // if (expenseDetails.getUser() != null &&
            // !expenseDetails.getUser().getId().equals(existingExpense.getUser().getId()))
            // {
            // existingExpense.getUser().getExpenses().remove(existingExpense);
            // GlobalLogger.info(ExpenseServiceImpl.class, "Removed expense from previous
            // user");
            // }

            // assert expenseDetails.getUser() != null;
            // User newUser = userService.findById(expenseDetails.getUser().getId())
            // .orElseThrow(() -> new IllegalArgumentException("User not found"));
            // newUser.getExpenses().add(existingExpense);
            // existingExpense.setUser(newUser);
            // GlobalLogger.info(ExpenseServiceImpl.class, "Associated expense with new user
            // id: {}", newUser.getId());

            existingExpense.setAmount(expenseDetails.getAmount());
            existingExpense.setDescription(expenseDetails.getDescription());
            existingExpense.setDate(expenseDetails.getDate());
            existingExpense.setCategory(expenseDetails.getCategory());

            Expense updatedExpense = expenseRepository.save(expenseDetails);
            GlobalLogger.info(ExpenseServiceImpl.class, "Expense updated successfully: {}", updatedExpense);
            return updatedExpense;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to update expense: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Expense> findById(Long id) {
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expense by id: {}", id);
        try {
            Optional<Expense> expense = expenseRepository.findById(id);
            if (expense.isEmpty()) {
                GlobalLogger.warn(ExpenseServiceImpl.class, "Expense with id {} not found", id);
                throw new IllegalArgumentException("Expense not found with id: " + id);
            }
            GlobalLogger.info(ExpenseServiceImpl.class, "Expense found successfully with id: {}", id);
            return expense;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to find expense: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Expense> findByUserId(Long userId) {
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {}", userId);

        try {
            User user = getUser(userId);
            List<Expense> expenses = new ArrayList<>(user.getExpenses());
            GlobalLogger.info(ExpenseServiceImpl.class, "Found {} expenses for user id: {}", expenses.size(), userId);
            return expenses;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to find expenses: {}", e.getMessage());
            throw e;
        }
    }

    public List<Expense> findByUserIdAndMonth(Long userId, int month) {

        ExpenseValidator.validateMonth(month);
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {} and month: {}", userId, month);

        return findByUserId(userId)
                .stream().filter(expense -> expense.getDate() != null &&
                        expense.getDate().getMonth().equals(Month.of(month)))
                .collect(Collectors.toList());
    }

    public List<Expense> findByUserIdAndYear(Long userId, int year) {
        ExpenseValidator.validateYear(year);
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {} and year: {}", userId, year);
        return findByUserId(userId).stream().filter(expense -> expense.getDate().getYear() == year)
                .collect(Collectors.toList());

    }

    public List<Expense> findByUserIdAndMonthAndYear(Long userId, int month, int year) {

        ExpenseValidator.validateMonth(month);
        ExpenseValidator.validateYear(year);
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {}, month: {} and year: {}", userId,
                month, year);
        return findByUserIdAndYear(userId, year).stream()
                .filter(expense -> expense.getDate().getMonth().equals(Month.of(month)))
                .collect(Collectors.toList());

    }

    public List<Expense> findByUserIdAndMonthAndYearAndCategoryId(Long userId, int month, int year,
            Long categoryId) {

        ExpenseValidator.validateMonth(month);
        ExpenseValidator.validateYear(year);
        ExpenseValidator.validateExpenseCategory(getExpenseCategoryUserIdByExpenseCategoryId(categoryId), userId);
        GlobalLogger.info(ExpenseServiceImpl.class,
                "Finding expenses by user id: {}, month: {}, year: {} and categoryId: {}", userId, month, year,
                categoryId);
        return findByUserIdAndMonthAndYear(userId, month, year)
                .stream().filter(expense -> expense.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());

    }

    public List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId) {

        ExpenseValidator.validateExpenseCategory(getExpenseCategoryUserIdByExpenseCategoryId(categoryId), userId);
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {} and categoryId: {}", userId,
                categoryId);
        return findByUserId(userId)
                .stream().filter(expense -> expense.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    public List<Expense> findByUserIdAndYearAndCategoryId(Long userId, int year, Long categoryId) {
        ExpenseValidator.validateYear(year);
        ExpenseValidator.validateExpenseCategory(getExpenseCategoryUserIdByExpenseCategoryId(categoryId), userId);
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {}, year: {} and categoryId: {}",
                userId, year, categoryId);
        return findByUserIdAndYear(userId, year)
                .stream().filter(expense -> expense.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());

    }

    public List<Expense> findByUserIdAndCategoryIdAndMonth(Long userId, Long categoryId, int month) {

        ExpenseValidator.validateMonth(month);
        ExpenseValidator.validateExpenseCategory(getExpenseCategoryUserIdByExpenseCategoryId(categoryId), userId);
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {}, month: {} and categoryId: {}",
                userId, month, categoryId);
        return findByUserIdAndMonth(userId, month)
                .stream().filter(expense -> expense.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());

    }

    public List<Expense> findByUserIdAndCategoryIdAndYear(Long userId, Long categoryId, int year) {
        ExpenseValidator.validateExpenseCategory(getExpenseCategoryUserIdByExpenseCategoryId(categoryId), userId);
        ExpenseValidator.validateYear(year);
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {}, year: {} and categoryId: {}",
                userId, year, categoryId);
        return findByUserIdAndYear(userId, year)
                .stream().filter(expense -> expense.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());

    }

    public List<Expense> findByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, int month,
            int year) {

        ExpenseValidator.validateMonth(month);
        ExpenseValidator.validateExpenseCategory(getExpenseCategoryUserIdByExpenseCategoryId(categoryId), userId);
        GlobalLogger.info(ExpenseServiceImpl.class,
                "Finding expenses by user id: {}, month: {}, year: {} and categoryId: {}", userId, month, year,
                categoryId);
        return findByUserIdAndMonthAndYear(userId, month, year)
                .stream().filter(expense -> expense.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
    }

    private User getUser(long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }

    private ExpenseCategory getCategory(long categoryId) {
        return expenseCategoryService.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("ExpenseCategory not found with id: " + categoryId));

    }

    private Long getExpenseCategoryUserIdByExpenseCategoryId(long categoryId) {
        return getCategory(categoryId).getUser().getId();
    }

}