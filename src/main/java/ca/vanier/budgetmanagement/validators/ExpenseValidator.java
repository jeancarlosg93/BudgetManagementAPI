package ca.vanier.budgetmanagement.validators;

import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.util.GlobalLogger;

public class ExpenseValidator {

    //Validates if the provided month is between 1 and 12
    public static void validateMonth(int month) {
        if (month < 1 || month > 12) {
            GlobalLogger.warn(ExpenseValidator.class, "Invalid month provided: {}", month);
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
    }

    //Validates if the year is between 1900 and next year
    public static void validateYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        if (year < 1900 || year > currentYear + 1) {
            GlobalLogger.warn(ExpenseValidator.class, "Invalid year provided: {}", year);
            throw new IllegalArgumentException("Year must be between 1900 and " + (currentYear + 1));
        }
    }

    //Validates if the expense amount is non-negative
    public static void validateAmount(double amount) {
        if (amount < 0) {
            GlobalLogger.warn(ExpenseValidator.class, "Invalid amount provided: {}", amount);
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }

    //Performs validation on an expense object
    public static void validateExpense(Expense expense) {
        if (expense == null) {
            throw new IllegalArgumentException("Expense cannot be null");
        }

        if (expense.getDate() == null) {
            throw new IllegalArgumentException("Expense date cannot be null");
        }

        validateAmount(expense.getAmount());
        validateMonth(expense.getDate().getMonthValue());
        validateYear(expense.getDate().getYear());
    }

    //Validates if the expense category belongs to the correct user
    public static void validateExpenseCategory(Long expenseCategoryUserId, Long userId) {
        if (expenseCategoryUserId != userId) {
            GlobalLogger.warn(ExpenseValidator.class,
                    "Invalid expense category provided, expense category belonging to userId {} cannot be added to expense belonging to userId {}",
                    expenseCategoryUserId, userId);
            throw new IllegalArgumentException(
                    String.format("Expense category userId (%d) and expense userId (%d) do not match",
                            expenseCategoryUserId, userId));
        }
    }
}