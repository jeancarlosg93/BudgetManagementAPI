package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.services.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/income")
public class IncomeController extends BaseController {


    final IncomeService incomeService;

    // request can be made to /api/income/user/{userId}
    // with optional query parameters incomeType, startDate, and endDate
    // startDate and endDate must be in the format yyyy-MM-dd
    // incomeType must be one of the following: SALARY, BONUS, INTEREST, DIVIDEND, OTHER
    // if no query parameters are provided, all incomes for the user will be returned
    // if any query parameters are invalid, a bad request response will be returned
    // if the user does not exist, a not found response will be returned
    // if the user has no incomes, an empty list will be returned
    //example request: /api/income/user/1?incomeType=SALARY&startDate=2021-01-01&endDate=2021-12-31

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getIncomes(@PathVariable Long userId,
                                        @RequestParam(required = false) String incomeType,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                        @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(incomeService.findWithFilters(userId, incomeType, startDate, endDate));
        } catch (IllegalArgumentException e) {
            return error("find.error.user.not.found", new Object[]{userId}, locale);
        }
    }

    @PostMapping("/save/user/{userId}")
    public ResponseEntity<?> saveIncome(@PathVariable Long userId, @RequestBody Income income,
            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            Income savedIncome = incomeService.save(userId,income);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedIncome);
        } catch (IllegalArgumentException e) {
            return error("income.error.save", null, locale);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIncome(@PathVariable Long id,
                                               @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            incomeService.deleteIncome(id);
            return success("income.deleted", locale);
        } catch (IllegalArgumentException e) {
            return error("income.error.delete", null, locale);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable Long id, @RequestBody Income incomeDetails,
                                          @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            Income updatedIncome = incomeService.updateExistingIncome(id, incomeDetails);
            return ResponseEntity.ok(updatedIncome);
        } catch (IllegalArgumentException e) {
            return error("income.error.not.found", new Object[]{id}, locale);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIncomeById(@PathVariable Long id,
                                           @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(incomeService.findById(id));
        } catch (IllegalArgumentException e) {
            return error("income.error.not.found", new Object[]{id}, locale);
        }
    }
}