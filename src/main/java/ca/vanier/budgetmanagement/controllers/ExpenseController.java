package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.services.ExpenseService;
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
@RequestMapping("/api/expense")
public class ExpenseController extends BaseController {

    final ExpenseService expenseService;

    // request can be made to /api/expense/user/{userId}`
    // with optional query parameters categoryId, startDate, and endDate
    // startDate and endDate must be in the format yyyy-MM-dd
    // if no query parameters are provided, all expense for the user will be returned
    // if any query parameters are invalid, a bad request response will be returned
    // if the user does not exist, a not found response will be returned
    // if the user has no incomes, an empty list will be returned
    // example request: /api/expense/user/1?categoryId=1&startDate=2021-01-01&endDate=2021-12-31
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getExpenses(@PathVariable Long userId, @RequestParam(required = false) Long categoryId,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                         @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(expenseService.findWithFilters(userId, categoryId, startDate, endDate));
        } catch (IllegalArgumentException e) {
            return error("find.error.user.not.found", new Object[]{userId}, locale);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllExpenses(@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            List<Expense> expenses = expenseService.findAll();
            if (expenses.isEmpty()) {
                return error("find.no.results", new Object[]{}, locale);
            }
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return error("find.error", null, locale);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveExpense(@RequestBody Expense expense,
                                         @RequestHeader(name = "Accept-Language", required = false) Locale locale) {

        try {
            Expense savedExpense = expenseService.save(expense);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
        } catch (IllegalArgumentException e) {
            return error("expense.error.save", null, locale);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id,
                                                @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            expenseService.deleteExpense(id);
            return success("expense.deleted", locale);
        } catch (IllegalArgumentException e) {
            return error("expense.error.delete", null, locale);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody Expense expenseDetails,
                                           @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            Expense updatedExpense = expenseService.updateExistingExpense(id, expenseDetails);
            return ResponseEntity.ok(updatedExpense);
        } catch (IllegalArgumentException e) {
            return error("expense.error.not.found", new Object[]{id}, locale);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long id,
                                            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(expenseService.findById(id));
        } catch (IllegalArgumentException e) {
            return error("expense.error.not.found", new Object[]{id}, locale);
        }
    }
}