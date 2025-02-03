package ca.vanier.budgetmanagement;

import ca.vanier.budgetmanagement.entities.Expense;
import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.ExpenseRepository;
import ca.vanier.budgetmanagement.repositories.ReportRepository;
import ca.vanier.budgetmanagement.services.UserService;
import ca.vanier.budgetmanagement.services.ExpenseCategoryService;
import ca.vanier.budgetmanagement.services.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @Mock
    private ExpenseCategoryService categoryService;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private User testUser;
    private ExpenseCategory testCategory;
    private Expense testExpense;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password", "USER", "John", "Doe", "john@example.com", "1234567890");
        testUser.setId(1L);

        testCategory = new ExpenseCategory("Groceries", testUser);
        testCategory.setId(1L);

        testExpense = new Expense(
                100.0,
                "Weekly groceries",
                testUser,
                LocalDate.now(),
                testCategory
        );
        testExpense.setId(1L);
    }

    @Test
    void save_NewExpense_Success() {
        // Arrange
        when(userService.findById(any())).thenReturn(Optional.of(testUser));
        when(categoryService.findById(any())).thenReturn(Optional.of(testCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        Expense savedExpense = expenseService.save(testExpense);

        // Assert
        assertNotNull(savedExpense);
        assertEquals(testExpense.getAmount(), savedExpense.getAmount());
        assertEquals(testExpense.getDescription(), savedExpense.getDescription());
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void save_InvalidCategory_ThrowsException() {
        // Arrange
        ExpenseCategory invalidCategory = new ExpenseCategory();
        invalidCategory.setId(99L);
        testExpense.setCategory(invalidCategory);

        when(userService.findById(any())).thenReturn(Optional.of(testUser));
        when(categoryService.findById(99L)).thenThrow(new IllegalArgumentException("Category not found"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> expenseService.save(testExpense));
    }

    @Test
    void findById_ExistingExpense_Success() {
        // Arrange
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        // Act
        Optional<Expense> found = expenseService.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(testExpense.getAmount(), found.get().getAmount());
    }

    @Test
    void findById_NonexistentExpense_ThrowsException() {
        // Arrange
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> expenseService.findById(99L));
    }

    @Test
    void deleteExpense_Success() {
        // Arrange
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        // Act
        expenseService.deleteExpense(1L);

        // Assert
        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void updateExistingExpense_Success() {
        // Arrange
        Expense updatedDetails = new Expense(
                200.0,
                "Updated groceries",
                testUser,
                LocalDate.now(),
                testCategory
        );
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(categoryService.findById(any())).thenReturn(Optional.of(testCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(updatedDetails);

        // Act
        Expense updated = expenseService.updateExistingExpense(1L, updatedDetails);

        // Assert
        assertNotNull(updated);
        assertEquals(updatedDetails.getAmount(), updated.getAmount());
        assertEquals(updatedDetails.getDescription(), updated.getDescription());
    }

    @Test
    void find_ByUserId_Success() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense);
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        testUser.setExpenses(expenses);

        // Act
        List<Expense> result = expenseService.find(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testExpense.getAmount(), result.get(0).getAmount());
    }

    @Test
    void findWithFilters_Success() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryService.findById(1L)).thenReturn(Optional.of(testCategory));
        testUser.setExpenses(Arrays.asList(testExpense));

        // Act
        List<Expense> result = expenseService.findWithFilters(1L, 1L, startDate, endDate);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(categoryService).findById(1L);
    }
}