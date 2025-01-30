package ca.vanier.budgetmanagement.repositories;

import ca.vanier.budgetmanagement.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}