package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.services.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    @Autowired
    BudgetService budgetService;


    @GetMapping("/all")
    public ResponseEntity<?> getAllBudgets() {

        return new ResponseEntity<>(budgetService.findAll(), HttpStatus.OK);
    }
}