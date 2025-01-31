package ca.vanier.budgetmanagement;

import ca.vanier.budgetmanagement.entities.*;
import ca.vanier.budgetmanagement.repositories.ReportRepository;
import ca.vanier.budgetmanagement.services.BudgetService;
import ca.vanier.budgetmanagement.services.ExpenseService;
import ca.vanier.budgetmanagement.services.IncomeService;
import ca.vanier.budgetmanagement.services.UserService;
import ca.vanier.budgetmanagement.services.impl.ReportServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserService userService;

    @Mock
    private IncomeService incomeService;

    @Mock
    private ExpenseService expenseService;
    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User testUser;
    private Report testReport;
    private List<Income> testIncomes;
    private List<Expense> testExpenses;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        // Initialize test user
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

        // Initialize dates
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 3, 31);

        // Initialize test incomes
        testIncomes = new ArrayList<>();
        Income income1 = new Income(5000.00, "Monthly Salary", testUser, LocalDate.of(2024, 1, 15), IncomeType.SALARY);
        Income income2 = new Income(2000.00, "Bonus", testUser, LocalDate.of(2024, 2, 1), IncomeType.BONUS);
        testIncomes.add(income1);
        testIncomes.add(income2);

        // Initialize test expenses
        testExpenses = new ArrayList<>();
        ExpenseCategory category = new ExpenseCategory("Housing", testUser);
        Expense expense1 = new Expense(2000.00, "Rent", testUser, LocalDate.of(2024, 1, 5), category);
        Expense expense2 = new Expense(500.00, "Utilities", testUser, LocalDate.of(2024, 2, 10), category);
        testExpenses.add(expense1);
        testExpenses.add(expense2);



        // Initialize test report
        testReport = new Report();
        testReport.setId(1L);
        testReport.setUser(testUser);
        testReport.setStartDate(startDate);
        testReport.setEndDate(endDate);
        testReport.setIncomes(testIncomes);
        testReport.setExpenses(testExpenses);
        testReport.setTotalIncome(7000.00);
        testReport.setTotalExpense(2500.00);
        testReport.setNetAmount(4500.00);
    }

    @Test
    void whenCreateReport_thenSuccess() {
        // Arrange
        when(userService.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(incomeService.findByUserIdAndMonthAndYear(eq(testUser.getId()), anyInt(), eq(2024)))
                .thenReturn(testIncomes);
        when(expenseService.findByUserIdAndMonthAndYear(eq(testUser.getId()), anyInt(), eq(2024)))
                .thenReturn(testExpenses);
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);

        // Act
        Report createdReport = reportService.createReport(testUser.getId(), startDate, endDate);

        // Assert
        assertNotNull(createdReport);
        assertEquals(testUser, createdReport.getUser());
        assertEquals(startDate, createdReport.getStartDate());
        assertEquals(endDate, createdReport.getEndDate());
        assertEquals(7000.00, createdReport.getTotalIncome());
        assertEquals(2500.00, createdReport.getTotalExpense());
        assertEquals(4500.00, createdReport.getNetAmount());
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void whenCreateReportWithInvalidUser_thenThrowException() {
        // Arrange
        when(userService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                reportService.createReport(99L, startDate, endDate)
        );
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    void whenGetReportById_thenSuccess() {
        // Arrange
        when(reportRepository.findById(testReport.getId())).thenReturn(Optional.of(testReport));

        // Act
        Report found = reportService.getReportById(testReport.getId());

        // Assert
        assertNotNull(found);
        assertEquals(testReport.getId(), found.getId());
        assertEquals(testReport.getUser(), found.getUser());
        verify(reportRepository).findById(testReport.getId());
    }

    @Test
    void whenGetReportByInvalidId_thenThrowException() {
        // Arrange
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                reportService.getReportById(99L)
        );
    }

    @Test
    void whenDeleteReport_thenSuccess() {
        // Arrange
        when(reportRepository.findById(testReport.getId())).thenReturn(Optional.of(testReport));
        doNothing().when(reportRepository).deleteById(testReport.getId());

        // Act
        reportService.deleteReport(testReport.getId());

        // Assert
        verify(reportRepository).deleteById(testReport.getId());
    }

    @Test
    void whenDeleteNonExistingReport_thenThrowException() {
        // Arrange
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                reportService.deleteReport(99L)
        );
        verify(reportRepository, never()).deleteById(anyLong());
    }

    @Test
    void whenGetAllReports_thenSuccess() {
        // Arrange
        List<Report> reports = List.of(testReport);
        when(reportRepository.findAll()).thenReturn(reports);

        // Act
        List<Report> found = reportService.getAllReports();

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals(testReport, found.get(0));
        verify(reportRepository).findAll();
    }

    @Test
    void whenGetReportsByUserId_thenSuccess() {
        // Arrange
        List<Report> userReports = List.of(testReport);
        testUser.setReports(userReports);
        when(userService.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // Act
        List<Report> found = reportService.getReportsByUserId(testUser.getId());

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
        assertEquals(testReport, found.get(0));
        verify(userService).findById(testUser.getId());
    }

    @Test
    void whenGetReportsByInvalidUserId_thenThrowException() {
        // Arrange
        when(userService.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                reportService.getReportsByUserId(99L)
        );
    }
}