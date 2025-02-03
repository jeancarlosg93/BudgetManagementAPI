package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.Budget;
import ca.vanier.budgetmanagement.services.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/budget")
public class BudgetController extends BaseController {

    final BudgetService budgetService;

    // request can be made to /api/budgets/user/{userId}`
    // with optional query parameters categoryId, startDate, and endDate
    // startDate and endDate must be in the format yyyy-MM-dd
    // if no query parameters are provided, all budgets for the user will be
    // returned
    // if any query parameters are invalid, a bad request response will be returned
    // if the user does not exist, a not found response will be returned
    // if the user has no budgets, an empty list will be returned
    // example request:
    // /api/budgets/user/1?categoryId=1&startDate=2021-01-01&endDate=2021-12-31
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBudgets(@PathVariable Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(budgetService.findWithFilters(userId, categoryId, startDate, endDate));
        } catch (IllegalArgumentException e) {
            return error("find.error.user.not.found", new Object[] { userId }, locale);
        }
    }

    // request can be made to /api/budgets/all
    // if no budgets are found, a not found response will be returned
    // if an error occurs, a bad request response will be returned
    // if budgets are found, they will be returned
    @GetMapping("/all")
    public ResponseEntity<?> getAllBudgets(@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            List<Budget> budgets = budgetService.findAll();
            if (budgets.isEmpty()) {
                return error("find.no.results", new Object[] {}, locale);
            }
            return ResponseEntity.ok(budgets);
        } catch (Exception e) {
            return error("find.error", null, locale);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBudget(@RequestBody Budget budget,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            Budget savedBudget = budgetService.save(budget);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBudget);
        } catch (IllegalArgumentException e) {
            return error("budget.error.save", null, locale);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetById(@PathVariable Long id,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(budgetService.findById(id));
        } catch (IllegalArgumentException e) {
            return error("budget.error.not.found", new Object[] { id }, locale);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            Budget updatedBudget = budgetService.updateExistingBudget(id, budgetDetails);
            return ResponseEntity.ok(updatedBudget);
        } catch (IllegalArgumentException e) {
            return error("budget.error.not.found", new Object[] { id }, locale);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBudget(@PathVariable Long id,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            budgetService.deleteBudget(id);
            return success("budget.deleted", locale);
        } catch (IllegalArgumentException e) {
            return error("budget.error.delete", null, locale);
        }
    }
}