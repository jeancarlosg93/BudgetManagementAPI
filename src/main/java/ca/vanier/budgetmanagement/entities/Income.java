package ca.vanier.budgetmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Income(double amount, String description, User user, LocalDate date, IncomeType type) {

        this.amount = amount;
        this.description = description;
        this.user = user;
        this.date = date;
        this.type = type;
    }
}