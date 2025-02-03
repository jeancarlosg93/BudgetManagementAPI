package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.Report;
import ca.vanier.budgetmanagement.repositories.ReportRepository;
import ca.vanier.budgetmanagement.services.*;
import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.validators.ReportValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {

    final private ReportRepository reportRepository;
    final private UserService userService;
    final private IncomeService incomeService;
    final private ExpenseService expenseService;

    //this method creates a report for a user based on the user id, start date, and end date
    //if the user does not exist, an exception is thrown
    @Transactional
    public Report createReport(Long userId, LocalDate startDate, LocalDate endDate) {
        GlobalLogger.info(ReportService.class, "Creating report for user id: " + userId);

        Report report = new Report();


        //calling the find method from the user service to get the user by id
        try {
            report.setUser(userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found")));
        } catch (IllegalArgumentException e) {
            GlobalLogger.error(ReportService.class, e.getMessage());
            throw e;
        }

        report.setStartDate(startDate);
        report.setEndDate(endDate);

        //here we call the find method from the income service
        // to get all incomes for the user within the specified date range
        List<Income> allIncomes = incomeService.find(
                userId,
                startDate,
                endDate);

        //here we call the find method from the expense service
        // to get all expenses for the user within the specified date range
        List<Expense> allExpenses = expenseService.find(
                userId,
                startDate,
                endDate);

        report.setIncomes(allIncomes);
        report.setExpenses(allExpenses);

        // here we calculate the total income and total expense for the report
        double totalIncome = allIncomes.stream()
                .mapToDouble(Income::getAmount)
                .sum();
        // here we calculate the total income and total expense for the report
        double totalExpense = allExpenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();


        // here we calculate the total income and total expense for the report
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setNetAmount(totalIncome - totalExpense);

        GlobalLogger.info(ReportService.class, "Report created: " + report);
        return reportRepository.save(report);
    }

    public Report getReportById(Long id) {
        GlobalLogger.info(ReportService.class, "Getting report by id: " + id);
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    }

    @Transactional
    public Report updateReport(Report report) {
        GlobalLogger.info(ReportService.class, "Updating report: " + report);
        ReportValidator.validateReport(report);

        Report existingReport = getReportById(report.getId());
        if (report.getStartDate() != null) {
            existingReport.setStartDate(report.getStartDate());
        }
        if (report.getEndDate() != null) {
            existingReport.setEndDate(report.getEndDate());
        }
        if (report.getIncomes() != null && !report.getIncomes().isEmpty()) {
            existingReport.setIncomes(report.getIncomes());
        }
        if (report.getExpenses() != null && !report.getExpenses().isEmpty()) {
            existingReport.setExpenses(report.getExpenses());
        }

        double totalIncome = report.getIncomes().stream()
                .mapToDouble(Income::getAmount)
                .sum();
        existingReport.setTotalIncome(totalIncome);

        double totalExpense = report.getExpenses().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        existingReport.setTotalExpense(totalExpense);
        GlobalLogger.info(ReportService.class, "Report updated: " + existingReport);
        return reportRepository.save(existingReport);
    }

    @Transactional
    public void deleteReport(Long id) {
        GlobalLogger.info(ReportService.class, "Deleting report by id: " + id);
        reportRepository.deleteById(getReportById(id).getId());
    }

    public List<Report> getAllReports() {
        GlobalLogger.info(ReportService.class, "Getting all reports");
        return reportRepository.findAll();

    }

    public List<Report> getReportsByUserId(Long userId) {
        GlobalLogger.info(ReportService.class, "Getting reports by user id: " + userId);

        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getReports();
    }

}