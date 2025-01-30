package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Income;

import java.util.List;
import java.util.Optional;

public interface IncomeService {

    Income save(Income income);

    List<Income> findAll();

    void deleteIncome(Long id);

    Income updateExistingIncome(Long id, Income incomeDetails);

    Optional<Income> findById(Long id);

    List<Income> findByUserId(Long userId);

    List<Income> findByUserIdAndMonth(Long userId, int month);

    List<Income> findByUserIdAndYear(Long userId, int year);

    List<Income> findByUserIdAndMonthAndYear(Long userId, int month, int year);

    List<Income> findByUserIdAndMonthAndYearAndIncomeType(Long userId, int month, int year, String IncomeType);

    List<Income> findByUserIdAndIncomeType(Long userId, String IncomeType);

    List<Income> findByUserIdAndYearAndIncomeType(Long userId, int year, String IncomeType);

    List<Income> findByUserIdAndIncomeTypeAndMonth(Long userId, String IncomeType, int month);

    List<Income> findByUserIdAndIncomeTypeAndYear(Long userId, String IncomeType, int year);

    List<Income> findByUserIdAndIncomeTypeAndMonthAndYear(Long userId, String IncomeType, int month, int year);


}