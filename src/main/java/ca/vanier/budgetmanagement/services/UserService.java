package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    void saveAll(List<User> users);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User updateExistingUser(Long id, User userDetails);

    List<User> findAll();

    void deleteUser(Long id);

}