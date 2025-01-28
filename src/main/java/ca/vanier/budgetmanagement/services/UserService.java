package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> findById(Long id);

    User updateExistingUser(Long id, User userDetails);

    List<User> findAll();

    void deleteUser(Long id);
}