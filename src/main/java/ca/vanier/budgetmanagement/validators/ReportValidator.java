package ca.vanier.budgetmanagement.validators;

import ca.vanier.budgetmanagement.entities.Report;

public class ReportValidator {
    public static void validateReport(Report report) {

        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }

        IncomeValidator.validateYear(report.getEndDate().getYear());
        IncomeValidator.validateMonth(report.getStartDate().getMonthValue());
        IncomeValidator.validateMonth(report.getEndDate().getMonthValue());
        IncomeValidator.validateYear(report.getStartDate().getYear());

        if (report.getUser() == null) {
            throw new IllegalArgumentException("Report user cannot be null");
        }

        if (report.getStartDate() == null) {
            throw new IllegalArgumentException("Report start date cannot be null");
        }

        if (report.getEndDate() == null) {
            throw new IllegalArgumentException("Report end date cannot be null");
        }

        if (report.getStartDate().isAfter(report.getEndDate())) {
            throw new IllegalArgumentException("Report start date cannot be after end date");
        }
    }

}