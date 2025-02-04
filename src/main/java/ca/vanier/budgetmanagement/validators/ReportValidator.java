package ca.vanier.budgetmanagement.validators;

import ca.vanier.budgetmanagement.entities.Report;

public class ReportValidator {

    //Performs validation on a report object
    public static void validateReport(Report report) {

        if (report == null) {
            throw new IllegalArgumentException("Report cannot be null");
        }
        //perform validation on the report start date and end date
        IncomeValidator.validateMonth(report.getStartDate().getMonthValue());
        IncomeValidator.validateYear(report.getStartDate().getYear());
        IncomeValidator.validateMonth(report.getEndDate().getMonthValue());
        IncomeValidator.validateYear(report.getEndDate().getYear());


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