package ca.vanier.budgetmanagement.validators;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.IncomeType;
import ca.vanier.budgetmanagement.util.GlobalLogger;


public class IncomeValidator {
    //Validates if the provided month is between 1 and 12
    public static void validateMonth(int month) {
        if (month < 1 || month > 12) {
            GlobalLogger.warn(IncomeValidator.class, "Invalid month provided: {}", month);
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
    }

    //Validates if the year is between 1900 and next year
    public static void validateYear(int year) {
        int currentYear = java.time.Year.now().getValue();
        if (year < 1900 || year > currentYear + 1) {
            GlobalLogger.warn(IncomeValidator.class, "Invalid year provided: {}", year);
            throw new IllegalArgumentException("Year must be between 1900 and " + (currentYear + 1));
        }
    }

    //Validates if the amount is non-negative
    public static void validateAmount(double amount) {
        if (amount < 0) {
            GlobalLogger.warn(IncomeValidator.class, "Invalid amount provided: {}", amount);
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
    //Performs validation on an income object
    public static void validateIncome(Income income) {
        if (income == null) {
            throw new IllegalArgumentException("Income cannot be null");
        }

        if (income.getDate() == null) {
            throw new IllegalArgumentException("Income date cannot be null");
        }

        if (income.getType() == null) {
            throw new IllegalArgumentException("Income type cannot be null");
        }

        if (income.getAmount() < 0) {
            throw new IllegalArgumentException("Income amount cannot be negative");
        }

        validateAmount(income.getAmount());
        validateMonth(income.getDate().getMonthValue());
        validateYear(income.getDate().getYear());
    }

    //Validates if the income type is valid
    public static void validateIncomeType(String incomeType) {
        try {
            IncomeType.valueOf(incomeType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            GlobalLogger.warn(IncomeValidator.class, "Invalid income type provided: {}", incomeType);
            throw new IllegalArgumentException("Invalid income type: " + incomeType);
        }
    }
}