package ca.vanier.budgetmanagement.repositories;

import ca.vanier.budgetmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}