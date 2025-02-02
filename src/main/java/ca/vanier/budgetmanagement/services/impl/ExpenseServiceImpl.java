package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.entities.Report;
import ca.vanier.budgetmanagement.repositories.ExpenseRepository;
import ca.vanier.budgetmanagement.repositories.ReportRepository;
import ca.vanier.budgetmanagement.services.ExpenseService;
import ca.vanier.budgetmanagement.services.UserService;
import ca.vanier.budgetmanagement.services.ExpenseCategoryService;

import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.validators.ExpenseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ReportRepository reportRepository;

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

        if (expense.getCategory() != null) {
            try {
                ExpenseCategory category = expenseCategoryService.findById(expense.getCategory().getId())
                        .orElseThrow(() -> new IllegalArgumentException("ExpenseCategory not found"));
                ExpenseValidator.validateExpenseCategory(category.getUser().getId(), expense.getUser().getId());
                expense.setCategory(category);
                GlobalLogger.info(ExpenseServiceImpl.class, "Associated expense with expenseCategory id: {}",
                        category.getId());
            } catch (IllegalArgumentException e) {
                GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to find expenseCategory for expense: {}",
                        e.getMessage());
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

            // Clear expense from any reports before deleteing
            List<Report> reports = reportRepository.findAllByUser(expense.getUser());
            for (Report report : reports) {
                if (report.getExpenses().contains(expense)) {
                    report.getExpenses().remove(expense);
                    reportRepository.save(report); // Save updated report
                }
            }

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

        GlobalLogger.info(ExpenseServiceImpl.class, "Updating expense with id: {}", id);

        try {
            Expense existingExpense = findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

            if (expenseDetails.getCategory() != null) {
                ExpenseCategory category = expenseCategoryService.findById(expenseDetails.getCategory().getId())
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Expense Cateory not found when attempting to update Expense"));
                ExpenseValidator.validateExpenseCategory(category.getUser().getId(), existingExpense.getUser().getId());
                existingExpense.setCategory(category);
            }

            existingExpense.setAmount(expenseDetails.getAmount());
            if (expenseDetails.getDescription() != null) {
                existingExpense.setDescription(expenseDetails.getDescription());
            }
            if (expenseDetails.getDate() != null) {
                existingExpense.setDate(expenseDetails.getDate());
            }
            ExpenseValidator.validateExpense(existingExpense);

            Expense updatedExpense = expenseRepository.save(existingExpense);
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
    public List<Expense> find(Long userId) {
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

    @Override
    public List<Expense> find(Long userid, LocalDate startDate, LocalDate endDate) {
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {} between dates: {} and {}", userid,
                startDate, endDate);
        try {
            return find(userid).stream()
                    .filter(expense -> expense.getDate() != null &&
                            (expense.getDate().isAfter(startDate) || expense.getDate().isEqual(startDate)) &&
                            (expense.getDate().isBefore(endDate) || expense.getDate().isEqual(endDate)))
                    .toList();
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to find expenses: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Expense> find(Long userid, LocalDate startDate, LocalDate endDate, Long category) {
        GlobalLogger.info(ExpenseServiceImpl.class,
                "Finding expenses by user id: {} between dates: {} and {} and category: {}", userid,
                startDate, endDate, category);
        try {
            return find(userid, startDate, endDate).stream()
                    .filter(expense -> expense.getCategory().getId().equals(category))
                    .toList();
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to find expenses: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Expense> find(Long userid, Long category) {
        GlobalLogger.info(ExpenseServiceImpl.class, "Finding expenses by user id: {} and category: {}", userid,
                category);
        try {
            return find(userid).stream()
                    .filter(expense -> expense.getCategory().getId().equals(category))
                    .toList();
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(ExpenseServiceImpl.class, "Failed to find expenses: {}", e.getMessage());
            throw e;
        }

    }

    @Override
    public List<Expense> findWithFilters(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate) {

        boolean hasCategoryId = categoryId != null;
        boolean hasDateRange = startDate != null && endDate != null;
        if (hasCategoryId) {
            ExpenseValidator.validateExpenseCategory(getExpenseCategoryUserIdByExpenseCategoryId(categoryId), userId);
        }
        try {
            if (hasCategoryId && hasDateRange) {
                return find(userId, startDate, endDate, categoryId);
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