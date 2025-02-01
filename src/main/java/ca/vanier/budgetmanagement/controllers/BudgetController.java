package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.Budget;
import ca.vanier.budgetmanagement.services.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    @Autowired
    BudgetService budgetService;


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

    @GetMapping("/find-by-user/{id}")
    public ResponseEntity<?> findByUserId(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(budgetService.findByUserId(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Budgets with user ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }



}