package ca.vanier.budgetmanagement.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "expense_categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "userId" })
})
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "userId", nullable = false)
    private Long userId; // Ensure this matches your user ID type

    public ExpenseCategory(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }
}
