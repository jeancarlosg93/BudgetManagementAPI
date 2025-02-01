package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.services.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired
    IncomeService incomeService;

    @GetMapping("/all")
    public ResponseEntity<List<Income>> getAllIncomes() {
        return new ResponseEntity<>(incomeService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/save/user/{userId}")
    public ResponseEntity<?> saveIncome(@PathVariable("userId") long userId, @RequestBody Income income) {
        try {
            Income savedIncome = incomeService.save(userId,income);
            return new ResponseEntity<>(savedIncome, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIncome(@PathVariable Long id) {
        try {
            incomeService.deleteIncome(id);
            return new ResponseEntity<>("Income deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable Long id, @RequestBody Income incomeDetails) {
        try {
            Income updatedIncome = incomeService.updateExistingIncome(id, incomeDetails);
            return new ResponseEntity<>(updatedIncome, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIncomeById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(incomeService.findById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getIncomesByUserId(@PathVariable Long userId) {
        try {
            List<Income> incomes = incomeService.findByUserId(userId);
            return new ResponseEntity<>(incomes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}