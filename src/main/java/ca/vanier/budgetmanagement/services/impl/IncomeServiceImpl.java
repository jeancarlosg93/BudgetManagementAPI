package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.IncomeRepository;
import ca.vanier.budgetmanagement.services.IncomeService;
import ca.vanier.budgetmanagement.services.UserService;
import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.validators.IncomeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class IncomeServiceImpl implements IncomeService {

    final private IncomeRepository incomeRepository;
    final private UserService userService;

    @Transactional
    @Override
    public Income save(long userId, Income income) {
        GlobalLogger.info(IncomeServiceImpl.class, "Saving income: {}", income.toString());

        IncomeValidator.validateIncome(income);

        if (income.getUser() != null) {
            try {
                User user = userService.findById(userId)
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

            if (incomeDetails.getUser() != null
                    && !incomeDetails.getUser().getId().equals(existingIncome.getUser().getId())) {
                existingIncome.getUser().getIncomes().remove(existingIncome);
                GlobalLogger.info(IncomeServiceImpl.class, "Removed income from previous user");
            }

            if (incomeDetails.getUser() != null) {
                User newUser = userService.findById(incomeDetails.getUser().getId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                newUser.getIncomes().add(existingIncome);
                existingIncome.setUser(newUser);
                GlobalLogger.info(IncomeServiceImpl.class, "Associated income with new user id: {}", newUser.getId());
            }

            existingIncome.setAmount(incomeDetails.getAmount());
            if (incomeDetails.getDescription() != null) {
                existingIncome.setDescription(incomeDetails.getDescription());
            }
            if (incomeDetails.getDate() != null) {
                existingIncome.setDate(incomeDetails.getDate());
            }
            if (incomeDetails.getType() != null) {
                existingIncome.setType(incomeDetails.getType());
            }

            IncomeValidator.validateIncome(existingIncome);

            Income updatedIncome = incomeRepository.save(existingIncome);
            GlobalLogger.info(IncomeServiceImpl.class, "Income updated successfully: {}", updatedIncome);
            return updatedIncome;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to update income: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Income> findWithFilters(long userId, String incomeType,
                                        LocalDate startDate, LocalDate endDate) {
        boolean hasIncomeType = incomeType != null;
        boolean hasDateRange = startDate != null && endDate != null;

        if (hasIncomeType && hasDateRange) {
            return find(userId, incomeType, startDate, endDate);
        } else if (hasDateRange) {
            return find(userId, startDate, endDate);
        } else if (hasIncomeType) {
            return find(userId, incomeType);
        } else {
            return find(userId);
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
    public List<Income> find(long userId) {
        GlobalLogger.info(IncomeServiceImpl.class, "Finding incomes by user id: {}", userId);

        try {
            Optional<User> user = userService.findById(userId);
            List<Income> incomes = new ArrayList<>();
            if (user.isPresent()) {
                incomes = user.get().getIncomes();
            }
            GlobalLogger.info(IncomeServiceImpl.class, "Found {} incomes for user id: {}", incomes.size(), userId);
            return incomes;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to find incomes: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to find incomes: " + e.getMessage());
        }
    }

    @Override
    public List<Income> find(long userid, LocalDate startDate, LocalDate endDate) {
        GlobalLogger.info(IncomeServiceImpl.class, "Finding incomes by user id: {}, start date: {} and end date: {}", userid, startDate, endDate);

        try {
            List<Income> incomes = find(userid).stream()
                    .filter(income -> income.getDate().isAfter(startDate) && income.getDate().isBefore(endDate))
                    .toList();
            GlobalLogger.info(IncomeServiceImpl.class, "Found {} incomes for user id: {}", incomes.size(), userid);
            return incomes;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to find incomes: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Income> find(Long userid, String incomeType, LocalDate startDate, LocalDate endDate) {
        IncomeValidator.validateIncomeType(incomeType);
        GlobalLogger.info(IncomeServiceImpl.class, "Finding incomes by user id: {}, start date: {}, end date: {} and income type: {}", userid, startDate, endDate, incomeType);

        try {
            List<Income> incomes = find(userid, startDate, endDate).stream()
                    .filter(income -> income.getType().name().equals(incomeType))
                    .toList();
            GlobalLogger.info(IncomeServiceImpl.class, "Found {} incomes for user id: {}", incomes.size(), userid);
            return incomes;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to find incomes: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Income> find(long userid, String incomeType) {
        IncomeValidator.validateIncomeType(incomeType);
        GlobalLogger.info(IncomeServiceImpl.class, "Finding incomes by user id: {} and income type: {}", userid, incomeType);

        try {
            List<Income> incomes =
                    find(userid).stream()
                            .filter(income -> income.getType().name().equals(incomeType))
                            .toList();
            GlobalLogger.info(IncomeServiceImpl.class, "Found {} incomes for user id: {}", incomes.size(), userid);
            return incomes;
        } catch (IllegalArgumentException e) {
            GlobalLogger.warn(IncomeServiceImpl.class, "Failed to find incomes: {}", e.getMessage());
            throw e;
        }
    }

    private User getUser(long userId) {
        return userService.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

    }

}