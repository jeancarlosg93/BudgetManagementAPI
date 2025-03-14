package ca.vanier.budgetmanagement.services.impl;

import ca.vanier.budgetmanagement.services.UserService;
import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User save(User user) {
        GlobalLogger.info(UserServiceImpl.class, "Saving user: {}", user);

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            GlobalLogger.warn(UserServiceImpl.class, "User already exists with username: {}", user.getUsername());
            throw new IllegalArgumentException("User already exists with username: " + user.getUsername());
        }

        if (user.getId() == null) {
            // Set the default role to user if not provided
            if (user.getRole() == null) {
                user.setRole(User.ROLE_USER);
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            GlobalLogger.info(UserServiceImpl.class, "User saved successfully with id: {}", savedUser.getId());
            return savedUser;
        }

        GlobalLogger.warn(UserServiceImpl.class, "User already exists with id: {}", user.getId());
        throw new IllegalArgumentException("Cannot save user with existing ID");
    }

    @Transactional
    @Override
    public void saveAll(List<User> users) {

        GlobalLogger.info(UserServiceImpl.class, "Saving all users: {}", users.toString());
        for (User user : users) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        GlobalLogger.info(UserServiceImpl.class, "All users saved successfully");
        userRepository.saveAll(users);
    }

    @Override
    public Optional<User> findById(Long id) {
        GlobalLogger.info(UserServiceImpl.class, "Finding user by id {}", id);

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            GlobalLogger.warn(UserServiceImpl.class, "User with id {} not found", id);
            throw new IllegalArgumentException("User not found");
        }
        GlobalLogger.info(UserServiceImpl.class, "User with id {} found successfully", id);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        GlobalLogger.info(UserServiceImpl.class, "Finding user by username {}", username);

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            GlobalLogger.warn(UserServiceImpl.class, "User with username {} not found", username);
            throw new RuntimeException("User not found");
        }
        GlobalLogger.info(UserServiceImpl.class, "User with username {} found successfully", username);
        return user;
    }


    // This method is used to update an existing user
    // It takes in the id of the user to be updated and the new details of the user
    @Transactional
    @Override
    public User updateExistingUser(Long id, User userDetails) {

        GlobalLogger.info(UserServiceImpl.class, "Updating user with id: {}", id);
        // Find the user by id, if not found throw an exception
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        // Update the user details if they are not null
        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }
        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        // Save the updated user
        User updatedUser = userRepository.save(user);
        GlobalLogger.info(UserServiceImpl.class, "User with id {} updated successfully {}", id, updatedUser.toString());
        return updatedUser;

    }

    @Override
    public List<User> findAll() {
        GlobalLogger.info(UserServiceImpl.class, "Fetching all users");
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {

        GlobalLogger.info(UserServiceImpl.class, "Deleting user with id: {}", id);

        if (userRepository.findById(id).isEmpty()) {
            GlobalLogger.warn(UserServiceImpl.class, "User with id {} not found", id);
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
        GlobalLogger.info(UserServiceImpl.class, "User with id {} deleted successfully", id);
    }

}