package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Report;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    Report createReport(Long userId, LocalDate startDate, LocalDate endDate);

    Report getReportById(Long id);

    void deleteReport(Long id);

    List<Report> getAllReports();

    List<Report> getReportsByUserId(Long userId);

    List<Report> getReportsByDateRange(Long userId, String startDate, String endDate);


}