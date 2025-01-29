package ca.vanier.budgetmanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @Enumerated
    private IncomeType type;
    private double amount;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Income(double amount, String description, User user, LocalDate date) {

        this.amount = amount;
        this.description = description;
        this.user = user;
        this.date = date;
    }
}