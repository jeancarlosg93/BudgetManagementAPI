package ca.vanier.budgetmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "report_incomes", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "income_id"))
    private List<Income> incomes;

    @ManyToMany
    @JoinTable(name = "report_expenses", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "expense_id"))
    private List<Expense> expenses;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private double totalIncome;
    private double totalExpense;
    private double netAmount;
}