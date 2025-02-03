package ca.vanier.budgetmanagement.controllers;


import ca.vanier.budgetmanagement.entities.Report;
import ca.vanier.budgetmanagement.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reports")
public class ReportController extends BaseController {

    final ReportService reportService;

    // request can be made to /api/reports/all
    // if no reports exist, a not found response will be returned
    // if reports exist, they will be returned
    // example request: GET http://localhost:8080/api/reports/all
    @GetMapping("/all")
    public ResponseEntity<?> getAllReports(@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            List<Report> reports = reportService.getAllReports();
            if (reports.isEmpty()) {
                return error("find.no.results", new Object[]{}, locale);
            }
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return error("find.error", null, locale);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id,
                                           @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(reportService.getReportById(id));
        } catch (IllegalArgumentException e) {
            return error("report.error.not.found", new Object[]{id}, locale);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestParam Long userId,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                          @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    reportService.createReport(userId, startDate, endDate));
        } catch (IllegalArgumentException e) {
            return error("report.error.create", null, locale);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReport(@PathVariable Long id,
                                               @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            reportService.deleteReport(id);
            return success("report.deleted", locale);
        } catch (IllegalArgumentException e) {
            return error("report.error.delete", null, locale);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReportsByUserId(@PathVariable Long userId,
                                                @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(reportService.getReportsByUserId(userId));
        } catch (IllegalArgumentException e) {
            return error("find.error.user.not.found", new Object[]{userId}, locale);
        }
    }
}