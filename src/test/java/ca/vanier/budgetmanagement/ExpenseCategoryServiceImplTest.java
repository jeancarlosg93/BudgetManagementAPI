package ca.vanier.budgetmanagement;

import ca.vanier.budgetmanagement.entities.ExpenseCategory;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.ExpenseCategoryRepository;
import ca.vanier.budgetmanagement.services.impl.ExpenseCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryServiceImplTest {

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @InjectMocks
    private ExpenseCategoryServiceImpl expenseCategoryService;

    private ExpenseCategory testCategory;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(
                "testuser",
                "password123",
                User.ROLE_USER,
                "Test",
                "User",
                "test@example.com",
                "123-456-7890"
        );
        testUser.setId(1L);

        testCategory = new ExpenseCategory("Groceries", testUser);
        testCategory.setId(1L);
    }

    @Test
    void whenSaveNewCategory_thenSuccess() {
        // Arrange
        ExpenseCategory newCategory = new ExpenseCategory("Entertainment", testUser);
        when(expenseCategoryRepository.findByNameAndUser("Entertainment", testUser)).thenReturn(null);
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(newCategory);

        // Act
        ExpenseCategory saved = expenseCategoryService.save(newCategory);

        // Assert
        assertNotNull(saved);
        assertEquals("Entertainment", saved.getName());
        verify(expenseCategoryRepository).save(newCategory);
    }

    @Test
    void whenSaveDuplicateCategory_thenThrowException() {
        // Arrange
        when(expenseCategoryRepository.findByNameAndUser("Groceries", testUser)).thenReturn(testCategory);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                expenseCategoryService.save(testCategory)
        );
    }

    @Test
    void whenSaveCategoryWithExistingId_thenThrowException() {
        // Arrange
        testCategory.setId(1L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                expenseCategoryService.save(testCategory)
        );
    }

    @Test
    void whenFindById_thenReturnCategory() {
        // Arrange
        when(expenseCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Optional<ExpenseCategory> found = expenseCategoryService.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Groceries", found.get().getName());
    }

    @Test
    void whenFindByInvalidId_thenThrowException() {
        // Arrange
        when(expenseCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                expenseCategoryService.findById(99L)
        );
    }

    @Test
    void whenUpdateCategory_thenSuccess() {
        // Arrange
        ExpenseCategory updatedCategory = new ExpenseCategory("Updated Groceries", testUser);
        when(expenseCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(expenseCategoryRepository.save(any(ExpenseCategory.class))).thenReturn(updatedCategory);

        // Act
        ExpenseCategory result = expenseCategoryService.updateExistingExpenseCategory(1L, updatedCategory);

        // Assert
        assertEquals("Updated Groceries", result.getName());
        verify(expenseCategoryRepository).save(any(ExpenseCategory.class));
    }

    @Test
    void whenUpdateNonExistentCategory_thenThrowException() {
        // Arrange
        when(expenseCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                expenseCategoryService.updateExistingExpenseCategory(99L, testCategory)
        );
    }

    @Test
    void whenFindAll_thenReturnList() {
        // Arrange
        List<ExpenseCategory> categories = Arrays.asList(
                testCategory,
                new ExpenseCategory("Entertainment", testUser)
        );
        when(expenseCategoryRepository.findAll()).thenReturn(categories);

        // Act
        List<ExpenseCategory> result = expenseCategoryService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(expenseCategoryRepository).findAll();
    }

    @Test
    void whenDeleteCategory_thenSuccess() {
        // Arrange
        when(expenseCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(expenseCategoryRepository).deleteById(1L);

        // Act
        expenseCategoryService.deleteExpenseCategory(1L);

        // Assert
        verify(expenseCategoryRepository).deleteById(1L);
    }

    @Test
    void whenDeleteNonExistentCategory_thenThrowException() {
        // Arrange
        when(expenseCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                expenseCategoryService.deleteExpenseCategory(99L)
        );
        verify(expenseCategoryRepository, never()).deleteById(anyLong());
    }
}