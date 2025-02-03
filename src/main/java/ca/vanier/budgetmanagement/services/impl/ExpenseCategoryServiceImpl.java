package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.services.ExpenseCategoryService;
import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.repositories.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    final private ExpenseCategoryRepository expenseCategoryRepository;

    @Override
    public ExpenseCategory save(ExpenseCategory expenseCategory) {
        GlobalLogger.info(ExpenseCategoryServiceImpl.class, "Saving ExpenseCategory: {}", expenseCategory);

        if (expenseCategoryRepository.findByNameAndUser(expenseCategory.getName(), expenseCategory.getUser()) != null) {
            GlobalLogger.warn(ExpenseCategoryServiceImpl.class,
                    "ExpenseCategory already exists with name: {}, and userId: {}",
                    expenseCategory.getName(), expenseCategory.getUser().getId());
            throw new IllegalArgumentException("Cannot save expenseCategory with existing name and user");
        }

        if (expenseCategory.getId() == null) {
            ExpenseCategory savedExpenseCategory = expenseCategoryRepository.save(expenseCategory);
            GlobalLogger.info(ExpenseCategoryServiceImpl.class, "ExpenseCategory saved successfully with id: {}",
                    savedExpenseCategory.getId());
            return savedExpenseCategory;
        }
        GlobalLogger.warn(ExpenseCategoryServiceImpl.class, "ExpenseCategory already exists with id: {}",
                expenseCategory.getId());
        throw new IllegalArgumentException("Cannot save ExpenseCategory with existing ID");
    }

    @Override
    public Optional<ExpenseCategory> findById(Long id) {
        GlobalLogger.info(ExpenseCategoryServiceImpl.class, "Finding ExpenseCategory by id {}", id);

        Optional<ExpenseCategory> expenseCategory = expenseCategoryRepository.findById(id);

        if (expenseCategory.isEmpty()) {
            GlobalLogger.warn(ExpenseCategoryServiceImpl.class, "ExpenseCategory with id {} not found", id);
            throw new RuntimeException("ExpenseCategory not found");
        }
        GlobalLogger.info(ExpenseCategoryServiceImpl.class, "ExpenseCategory with id {} found successfully", id);
        return expenseCategory;
    }

    @Override
    public ExpenseCategory updateExistingExpenseCategory(Long id, ExpenseCategory expenseCategoryDetails) {
        GlobalLogger.info(ExpenseCategoryServiceImpl.class, "Updating ExpenseCategory with id: {}", id);
        ExpenseCategory expenseCategory = expenseCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ExpenseCategory not found"));
        if (expenseCategoryDetails.getName() != null) {
            expenseCategory.setName(expenseCategoryDetails.getName());
        }
        expenseCategoryRepository.save(expenseCategory);
        GlobalLogger.info(ExpenseCategoryServiceImpl.class, "ExpenseCategory with id {} updated successfully {}", id,
                expenseCategory.toString());
        return expenseCategory;
    }

    @Override
    public List<ExpenseCategory> findAll() {
        GlobalLogger.info(ExpenseCategoryServiceImpl.class, "Fetching all ExpenseCategories");
        return expenseCategoryRepository.findAll();
    }

    @Override
    public void deleteExpenseCategory(Long id) {

        GlobalLogger.info(ExpenseCategoryServiceImpl.class, "Deleting ExpenseCategory with id: {}", id);

        if (expenseCategoryRepository.findById(id).isEmpty()) {
            GlobalLogger.warn(ExpenseCategoryServiceImpl.class, "ExpenseCategory with id {} not found", id);
            throw new RuntimeException("ExpenseCategory not found");
        }
        expenseCategoryRepository.deleteById(id);
        GlobalLogger.info(ExpenseCategoryServiceImpl.class, "ExpenseCategory with id {} deleted successfully", id);
    }
}