package ca.vanier.budgetmanagement.controllers;


import ca.vanier.budgetmanagement.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReport(Long userId, LocalDate startDate, LocalDate endDate) {
        return ResponseEntity.ok(reportService.createReport(userId, startDate, endDate));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteReport(Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok("Report deleted");
    }


}