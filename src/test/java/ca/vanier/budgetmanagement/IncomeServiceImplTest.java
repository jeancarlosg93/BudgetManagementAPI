package ca.vanier.budgetmanagement;

import ca.vanier.budgetmanagement.entities.Income;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.IncomeRepository;
import ca.vanier.budgetmanagement.services.impl.IncomeServiceImpl;
import ca.vanier.budgetmanagement.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ca.vanier.budgetmanagement.entities.IncomeType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceImplTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private IncomeServiceImpl incomeService;

    private Income testIncome;
    private User testUser;
    private List<Income> testIncomes;

    @BeforeEach
    void setUp() {
        testUser = new User(
                "testuser",
                "password123",
                User.ROLE_USER,
                "Test",
                "User",
                "test@example.com",
                "123-456-7890");
        testUser.setId(1L);

        testIncome = new Income(
                5000.00,
                "Monthly Salary",
                testUser,
                LocalDate.now(),
                SALARY);
        testIncome.setId(1L);

        Income secondIncome = new Income(
                3000.00,
                "Part-time work",
                testUser,
                LocalDate.now(),
                BONUS);
        secondIncome.setId(2L);

        testIncomes = new ArrayList<>();
        testIncomes.add(testIncome);
        testIncomes.add(secondIncome);

        testUser.setIncomes(new ArrayList<>(testIncomes));
    }

    @Test
    void whenSaveIncome_withValidUser_thenSuccess() {
        // Arrange
        when(userService.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(incomeRepository.save(any(Income.class))).thenReturn(testIncome);

        // Act
        Income savedIncome = incomeService.save(testIncome);

        // Assert
        assertNotNull(savedIncome);
        assertEquals(testIncome.getAmount(), savedIncome.getAmount());
        assertEquals(testIncome.getDescription(), savedIncome.getDescription());
        verify(incomeRepository).save(testIncome);
        verify(userService).findById(testUser.getId());
    }

    @Test
    void whenFindAll_thenReturnIncomeList() {
        // Arrange
        when(incomeRepository.findAll()).thenReturn(testIncomes);

        // Act
        List<Income> found = incomeService.findAll();

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(2, found.size());
        verify(incomeRepository).findAll();
    }

    @Test
    void whenDeleteIncome_thenSuccess() {
        // Arrange
        when(incomeRepository.findById(testIncome.getId())).thenReturn(Optional.of(testIncome));
        doNothing().when(incomeRepository).deleteById(testIncome.getId());

        // Act
        incomeService.deleteIncome(testIncome.getId());

        // Assert
        verify(incomeRepository).deleteById(testIncome.getId());
        assertFalse(testUser.getIncomes().contains(testIncome));
    }

    @Test
    void whenUpdateIncome_thenSuccess() {
        // Arrange
        Income updatedIncome = new Income(
                6000.00,
                "Updated Salary",
                testUser,
                LocalDate.now(),
                DIVIDEND);
        updatedIncome.setId(testIncome.getId());
        updatedIncome.setType(SALARY);

        when(incomeRepository.findById(testIncome.getId())).thenReturn(Optional.of(testIncome));
        when(userService.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(incomeRepository.save(any(Income.class))).thenReturn(updatedIncome);

        // Act
        Income result = incomeService.updateExistingIncome(testIncome.getId(), updatedIncome);

        // Assert
        assertNotNull(result);
        assertEquals(updatedIncome.getAmount(), result.getAmount());
        assertEquals(updatedIncome.getDescription(), result.getDescription());
        assertEquals(updatedIncome.getType(), result.getType());
        verify(incomeRepository).save(any(Income.class));
    }

    @Test
    void whenUpdateIncome_withInvalidId_thenThrowException() {
        // Arrange
        when(incomeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            incomeService.updateExistingIncome(99L, testIncome);
        });
        verify(incomeRepository, never()).save(any());
    }

    @Test
    void whenFindById_thenReturnIncome() {
        // Arrange
        when(incomeRepository.findById(testIncome.getId())).thenReturn(Optional.of(testIncome));

        // Act
        Optional<Income> found = incomeService.findById(testIncome.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(testIncome, found.get());
        verify(incomeRepository).findById(testIncome.getId());
    }

    @Test
    void whenFindByUserId_thenReturnIncomes() {
        // Arrange
        when(userService.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        List<Income> found = incomeService.find(testUser.getId());

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(2, found.size()); // Since we added 2 incomes in setUp
        verify(userService).findById(testUser.getId());
    }

}