package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.IncomeRepository;
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

        if (income.getUser() != null) {
            User user = userService.findById(income.getUser().getId()).orElseThrow(() -> new IllegalArgumentException("User notfound"));
            user.getIncomes().add(income);
            income.setUser(user);
        }

        return incomeRepository.save(income);
    }

    @Override
    public List<Income> findAll() {
        return incomeRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteIncome(Long id) {
        Income income = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Income not found"));

        if (income.getUser() != null) {
            income.getUser().getIncomes().remove(income);
            income.setUser(null);
        }

        incomeRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Income updateExistingIncome(Long id, Income incomeDetails) {

        Income existingIncome = findById(id).orElseThrow(() -> new IllegalArgumentException("Income not found"));

        if (incomeDetails.getUser() != null && !incomeDetails.getUser().getId().equals(existingIncome.getUser().getId())) {
            if (existingIncome.getUser() != null) {
                existingIncome.getUser().getIncomes().remove(existingIncome);
            }

        }

        User newUser = userService.findById(incomeDetails.getUser().getId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        newUser.getIncomes().add(existingIncome);
        existingIncome.setUser(newUser);

        existingIncome.setAmount(incomeDetails.getAmount());
        existingIncome.setDescription(incomeDetails.getDescription());
        existingIncome.setDate(incomeDetails.getDate());
        existingIncome.setType(incomeDetails.getType());

        return incomeRepository.save(incomeDetails);
    }

    @Override
    public Optional<Income> findById(Long id) {
        return Optional.ofNullable(incomeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Income not found with id: " + id)));
    }

    @Override
    public List<Income> findByUserId(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return new ArrayList<>(user.getIncomes());

    }
}