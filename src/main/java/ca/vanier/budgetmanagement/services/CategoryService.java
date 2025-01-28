package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category save(Category category);

    void saveAll(List<Category> categories);

    Optional<Category> findById(Long id);

    Category updateExistingCategory(Long id, Category categoryDetails);

    List<Category> findAll();

    void deleteCategory(Long id);

}