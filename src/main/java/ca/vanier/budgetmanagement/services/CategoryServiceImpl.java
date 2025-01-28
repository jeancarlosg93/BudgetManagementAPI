package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.entities.Category;
import ca.vanier.budgetmanagement.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category save(Category category) {
        GlobalLogger.info(CategoryServiceImpl.class, "Saving category: {}", category);

        if (categoryRepository.findByName(category.getName()) != null) {
            GlobalLogger.warn(CategoryServiceImpl.class, "Category already exists with name: {}", category.getName());
            throw new IllegalArgumentException("Cannot save category with existing name");
        }

        if (category.getId() == null) {
            Category savedCategory = categoryRepository.save(category);
            GlobalLogger.info(CategoryServiceImpl.class, "Category saved successfully with id: {}",
                    savedCategory.getId());
            return savedCategory;
        }
        GlobalLogger.warn(CategoryServiceImpl.class, "Category already exists with id: {}", category.getId());
        throw new IllegalArgumentException("Cannot save category with existing ID");
    }

    @Override
    public void saveAll(List<Category> categorys) {
        GlobalLogger.info(CategoryServiceImpl.class, "Saving all categorys: {}", categorys.toString());
        categoryRepository.saveAll(categorys);
    }

    @Override
    public Optional<Category> findById(Long id) {
        GlobalLogger.info(CategoryServiceImpl.class, "Finding category by id {}", id);

        Optional<Category> category = categoryRepository.findById(id);

        if (category.isEmpty()) {
            GlobalLogger.warn(CategoryServiceImpl.class, "Category with id {} not found", id);
            throw new RuntimeException("Category not found");
        }
        GlobalLogger.info(CategoryServiceImpl.class, "Category with id {} found successfully", id);
        return category;
    }

    @Override
    public Category updateExistingCategory(Long id, Category categoryDetails) {
        GlobalLogger.info(CategoryServiceImpl.class, "Updating category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (categoryDetails.getName() != null) {
            category.setName(categoryDetails.getName());
        }
        categoryRepository.save(category);
        GlobalLogger.info(CategoryServiceImpl.class, "Category with id {} updated successfully {}", id,
                category.toString());
        return category;
    }

    @Override
    public List<Category> findAll() {
        GlobalLogger.info(CategoryServiceImpl.class, "Fetching all categorys");
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {

        GlobalLogger.info(CategoryServiceImpl.class, "Deleting category with id: {}", id);

        if (categoryRepository.findById(id).isEmpty()) {
            GlobalLogger.warn(CategoryServiceImpl.class, "Category with id {} not found", id);
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
        GlobalLogger.info(CategoryServiceImpl.class, "Category with id {} deleted successfully", id);
    }
}