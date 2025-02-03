package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.services.ExpenseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/expense-category")
public class ExpenseCategoryController extends BaseController {

    final ExpenseCategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategories(@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            List<ExpenseCategory> categories = categoryService.findAll();
            if (categories.isEmpty()) {
                return error("find.no.results", new Object[]{}, locale);
            }
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return error("find.error", null, locale);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id,
                                             @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(categoryService.findById(id));
        } catch (IllegalArgumentException e) {
            return error("category.error.not.found", new Object[]{id}, locale);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCategory(@RequestBody ExpenseCategory category,
                                          @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            ExpenseCategory savedCategory = categoryService.save(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (IllegalArgumentException e) {
            return error("category.error.save", null, locale);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody ExpenseCategory categoryDetails,
                                            @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            ExpenseCategory updatedCategory = categoryService.updateExistingExpenseCategory(id, categoryDetails);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return error("category.error.not.found", new Object[]{id}, locale);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id,
                                                 @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            categoryService.deleteExpenseCategory(id);
            return success("category.deleted", locale);
        } catch (IllegalArgumentException e) {
            return error("category.error.delete", null, locale);
        }
    }
}