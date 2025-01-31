package ca.vanier.budgetmanagement.repositories;

import ca.vanier.budgetmanagement.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
}