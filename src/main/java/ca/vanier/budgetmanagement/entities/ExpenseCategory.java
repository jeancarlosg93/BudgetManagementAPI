package ca.vanier.budgetmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "expense_categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "user_Id" })
})
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column in the expense_categories table
    private User user; // Replaced Long userId with a User entity reference

    public ExpenseCategory(String name, User user) {
        this.name = name;
        this.user = user;
    }
}
