package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.services.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired
    IncomeService incomeService;

    // request can be made to /api/income/user/{userId}
    // with optional query parameters incomeType, startDate, and endDate
    // startDate and endDate must be in the format yyyy-MM-dd
    // incomeType must be one of the following: SALARY, BONUS, INTEREST, DIVIDEND,
    // OTHER
    // if no query parameters are provided, all incomes for the user will be
    // returned
    // if any query parameters are invalid, a bad request response will be returned
    // if the user does not exist, a not found response will be returned
    // if the user has no incomes, an empty list will be returned
    // example request:
    // /api/income/user/1?incomeType=SALARY&startDate=2021-01-01&endDate=2021-12-31

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Income>> findIncomes(
            @PathVariable Long userId,
            @RequestParam(required = false) String incomeType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Income> incomes = incomeService.findWithFilters(userId, incomeType, startDate, endDate);
            return ResponseEntity.ok(incomes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveIncome(@RequestBody Income income) {
        try {
            Income savedIncome = incomeService.save(income);
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

}