package ca.vanier.budgetmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    private double amount;
    private String name;
    private String description;

    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ExpenseCategory category;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @Transient
    private double actualExpenses;

    @Getter
    @Transient
    private double remainingAmount;

    public void setActualExpenses(double actualExpenses) {
        this.actualExpenses = actualExpenses;
        this.remainingAmount = this.amount - actualExpenses;
    }

    public String getStatus() {
        return remainingAmount >= 0 ? "Within Budget" : "Over Budget";
    }

    public Budget(double amount, String name, String description, User user,
            LocalDate startDate, LocalDate endDate, ExpenseCategory category) {
        this.amount = amount;
        this.name = name;
        this.description = description;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
    }
}