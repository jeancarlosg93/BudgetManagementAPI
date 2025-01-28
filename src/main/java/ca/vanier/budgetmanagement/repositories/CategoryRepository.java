package ca.vanier.budgetmanagement.repositories;

import ca.vanier.budgetmanagement.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
}