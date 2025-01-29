package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.IncomeRepository;
import ca.vanier.budgetmanagement.util.GlobalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserService userService;

    @Transactional
    @Override
    public Income save(Income income) {
        GlobalLogger.info(IncomeServiceImpl.class, "Saving income: {}", income.toString());

        if (income.getUser() != null) {
            try {
                User user = userService.findById(income.getUser().getId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                user.getIncomes().add(income);
                income.setUser(user);
                GlobalLogger.info(IncomeServiceImpl.class, "Associated income with user id: {}", user.getId());
            } catch (IllegalArgumentException e) {
                GlobalLogger.warn(IncomeServiceImpl.class, "Failed to find user for income: {}", e.getMessage());
                throw e;
            }
        }

        Income savedIncome = incomeRepository.save(income);
        GlobalLogger.info(IncomeServiceImpl.class, "Income saved successfully with id: {}", savedIncome.getId());
        return savedIncome;
    }

    @Override
    public List<Income> findAll() {
        GlobalLogger.info(IncomeServiceImpl.class, "Fetching all incomes");
        return incomeRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteIncome(Long id) {
        GlobalLogger.info(IncomeServiceImpl.class, "Deleting income with id: {}", id);

        try {
            Income income = findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Income not found"));

            if (income.getUser() != null) {
                income.getUser().getIncomes().remove(income);
                income.setUser(null);
                GlobalLogger.info(IncomeServiceImpl.class, "Removed income association from user");
            }

            incomeRepository.deleteById(id);
            GlobalLogger.info(IncomeServiceImpl.class, "Income with id {} deleted successfully", id);
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to delete income: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    @Override
    public Income updateExistingIncome(Long id, Income incomeDetails) {
        GlobalLogger.info(IncomeServiceImpl.class, "Updating income with id: {}", id);

        try {
            Income existingIncome = findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Income not found"));

            if (incomeDetails.getUser() != null && !incomeDetails.getUser().getId().equals(existingIncome.getUser().getId())) {
                if (existingIncome.getUser() != null) {
                    existingIncome.getUser().getIncomes().remove(existingIncome);
                    GlobalLogger.info(IncomeServiceImpl.class, "Removed income from previous user");
                }
            }

            User newUser = userService.findById(incomeDetails.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            newUser.getIncomes().add(existingIncome);
            existingIncome.setUser(newUser);
            GlobalLogger.info(IncomeServiceImpl.class, "Associated income with new user id: {}", newUser.getId());

            existingIncome.setAmount(incomeDetails.getAmount());
            existingIncome.setDescription(incomeDetails.getDescription());
            existingIncome.setDate(incomeDetails.getDate());
            existingIncome.setType(incomeDetails.getType());

            Income updatedIncome = incomeRepository.save(incomeDetails);
            GlobalLogger.info(IncomeServiceImpl.class, "Income updated successfully: {}", updatedIncome);
            return updatedIncome;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to update income: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Income> findById(Long id) {
        GlobalLogger.info(IncomeServiceImpl.class, "Finding income by id: {}", id);
        try {
            Optional<Income> income = incomeRepository.findById(id);
            if (income.isEmpty()) {
                GlobalLogger.warn(IncomeServiceImpl.class, "Income with id {} not found", id);
                throw new IllegalArgumentException("Income not found with id: " + id);
            }
            GlobalLogger.info(IncomeServiceImpl.class, "Income found successfully with id: {}", id);
            return income;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to find income: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Income> findByUserId(Long userId) {
        GlobalLogger.info(IncomeServiceImpl.class, "Finding incomes for user id: {}", userId);
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
            List<Income> incomes = new ArrayList<>(user.getIncomes());
            GlobalLogger.info(IncomeServiceImpl.class, "Found {} incomes for user id: {}", incomes.size(), userId);
            return incomes;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to find incomes for user: {}", e.getMessage());
            throw e;
        }
    }
}