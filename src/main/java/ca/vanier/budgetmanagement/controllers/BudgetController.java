package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.Budget;
import ca.vanier.budgetmanagement.services.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    @Autowired
    BudgetService budgetService;

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
    public ResponseEntity<?> find(@PathVariable Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            return new ResponseEntity<>(budgetService.findWithFilters(userId, categoryId, startDate, endDate),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("No budgets found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBudgets() {
        try {
            return new ResponseEntity<>(budgetService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("No budgets found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Budget budget) {
        try {
            return new ResponseEntity<>(budgetService.save(budget), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(budgetService.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Budget with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails) {
        try {
            return new ResponseEntity<>(budgetService.updateExistingBudget(id, budgetDetails), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable long id) {
        try {
            budgetService.deleteBudget(id);
            return new ResponseEntity<>("Budget deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting the budget", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}