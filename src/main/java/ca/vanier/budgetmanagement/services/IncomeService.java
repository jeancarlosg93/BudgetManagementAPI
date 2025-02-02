package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Income;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeService {

    Income save(Income income);

    List<Income> findAll();

    void deleteIncome(Long id);

    Income updateExistingIncome(Long id, Income incomeDetails);

    Optional<Income> findById(Long id);

    List<Income> find(long userid);

    List<Income> find(long userid, LocalDate startDate, LocalDate endDate);

    List<Income> find(Long userid, String incomeType, LocalDate startDate, LocalDate endDate);

    List<Income> find(long userid, String incomeType);

    List<Income> findWithFilters(long userId, String incomeType, LocalDate startDate, LocalDate endDate);

}