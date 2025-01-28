package ca.vanier.budgetmanagement.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;
    private String username;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;


    public User(String username, String password, String role, String firstName, String lastName, String email, String phone) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

//    @OneToMany(cascade = CascadeType.ALL,
//    mappedBy = "user")
//    private List<Budget> budget;


}