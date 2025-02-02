package ca.vanier.budgetmanagement.repositories;

import ca.vanier.budgetmanagement.entities.Report;
import ca.vanier.budgetmanagement.entities.User;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByUser(User user);
}