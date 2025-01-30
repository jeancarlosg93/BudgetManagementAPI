package ca.vanier.budgetmanagement.repositories;

import ca.vanier.budgetmanagement.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}