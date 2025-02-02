package ca.vanier.budgetmanagement.repositories;

import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    ExpenseCategory findByNameAndUser(String name, User user);
}