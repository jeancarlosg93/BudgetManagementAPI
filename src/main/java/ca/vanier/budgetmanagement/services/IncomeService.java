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
//
//    List<Income> findByUserIdAndMonth(Long userId, int month);
//
//    List<Income> findByUserIdAndYear(Long userId, int year);
//
//    List<Income> findByUserIdAndMonthAndYear(Long userId, int month, int year);
//
//    List<Income> findByUserIdAndMonthAndYearAndCategory(Long userId, int month, int year, String category);
//
//    List<Income> findByUserIdAndCategory(Long userId, String category);
//
//    List<Income> findByUserIdAndYearAndCategory(Long userId, int year, String category);
//
//    List<Income> findByUserIdAndCategoryAndMonth(Long userId, String category, int month);
//
//    List<Income> findByUserIdAndCategoryAndYear(Long userId, String category, int year);
//
//    List<Income> findByUserIdAndCategoryAndMonthAndYear(Long userId, String category, int month, int year);
//

}