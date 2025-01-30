package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.Report;
import ca.vanier.budgetmanagement.repositories.ReportRepository;
import ca.vanier.budgetmanagement.services.ExpenseService;
import ca.vanier.budgetmanagement.services.IncomeService;
import ca.vanier.budgetmanagement.services.ReportService;
import ca.vanier.budgetmanagement.services.UserService;
import ca.vanier.budgetmanagement.validators.ReportValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportRepository reportRepository;
    @Autowired
    UserService userService;
    @Autowired
    IncomeService incomeService;
    @Autowired
    ExpenseService expenseService;

    @Transactional
    public Report createReport(Long userId, LocalDate startDate, LocalDate endDate) {


        Report report = new Report();

        report.setUser(userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found")));

        report.setStartDate(startDate);
        report.setEndDate(endDate);

        List<Income> allIncomes = new ArrayList<>();
        List<Expense> allExpenses = new ArrayList<>();


        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            int month = currentDate.getMonthValue();
            int year = currentDate.getYear();

            List<Income> monthlyIncomes = incomeService.findByUserIdAndMonthAndYear(
                    userId,
                    month,
                    year
            );

            List<Expense> monthlyExpenses = expenseService.findByUserIdAndMonthAndYear(
                    userId,
                    month,
                    year
            );

            allIncomes.addAll(monthlyIncomes);
            allExpenses.addAll(monthlyExpenses);


            currentDate = currentDate.plusMonths(1);
        }


        report.setIncomes(allIncomes);
        report.setExpenses(allExpenses);

        double totalIncome = allIncomes.stream()
                .mapToDouble(Income::getAmount)
                .sum();

        double totalExpense = allExpenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);

        return reportRepository.save(report);
    }


    public Report getReportById(Long id) {

        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
    }

    @Transactional
    public Report updateReport(Report report) {
        ReportValidator.validateReport(report);

        Report existingReport = getReportById(report.getId());
        existingReport.setStartDate(report.getStartDate());
        existingReport.setEndDate(report.getEndDate());
        existingReport.setIncomes(report.getIncomes());
        existingReport.setExpenses(report.getExpenses());


        double totalIncome = report.getIncomes().stream()
                .mapToDouble(Income::getAmount)
                .sum();
        existingReport.setTotalIncome(totalIncome);

        double totalExpense = report.getExpenses().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
        existingReport.setTotalExpense(totalExpense);


        return reportRepository.save(existingReport);
    }

    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(getReportById(id).getId());
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();

    }

    public List<Report> getReportsByUserId(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getReports();
    }

    public List<Report> getReportsByDateRange(Long userId, String startDate, String endDate) {

        return null;
    }
}