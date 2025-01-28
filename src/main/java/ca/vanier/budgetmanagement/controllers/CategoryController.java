package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.Category;
import ca.vanier.budgetmanagement.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {

        try {
            return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("No categorys found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        try {
            return new ResponseEntity<>(categoryService.updateExistingCategory(id, categoryDetails), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(categoryService.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Category with ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
        try {
            categoryService.deleteCategory(id);
            return new ResponseEntity<>("Category deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting the category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Category category) {
        try {
            return new ResponseEntity<>(categoryService.save(category), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}