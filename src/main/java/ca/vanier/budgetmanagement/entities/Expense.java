package ca.vanier.budgetmanagement.entities;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ExpenseCategory category;

    private double amount;
    private String description;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Expense(double amount, String description, User user, LocalDate date, ExpenseCategory category) {

        this.amount = amount;
        this.description = description;
        this.user = user;
        this.date = date;
        this.category = category;
    }
}
