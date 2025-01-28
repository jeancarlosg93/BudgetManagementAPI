package ca.vanier.budgetmanagement.services;

import ca.vanier.budgetmanagement.util.GlobalLogger;
import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(User user) {
        GlobalLogger.info(UserServiceImpl.class, "Saving user: {}", user);


        if (user.getId() != null) {
            GlobalLogger.warn(UserServiceImpl.class, "User id cannot be null");
            throw new IllegalArgumentException("User cannot be null or empty");
        }
        User savedUser = userRepository.save(user);
        GlobalLogger.info(UserServiceImpl.class, "User saved successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public void saveAll(List<User> users) {
        GlobalLogger.info(UserServiceImpl.class, "Saving all users: {}", users.toString());
        userRepository.saveAll(users);
    }

    @Override
    public Optional<User> findById(Long id) {
        GlobalLogger.info(UserServiceImpl.class, "Finding user by id {}", id);

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            GlobalLogger.warn(UserServiceImpl.class, "User with id {} not found", id);
            throw new RuntimeException("User not found");
        }
        GlobalLogger.info(UserServiceImpl.class, "User with id {} found successfully", id);
        return user;
    }

    @Override
    public User updateExistingUser(Long id, User userDetails) {
        GlobalLogger.info(UserServiceImpl.class, "Updating user with id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.save(user);
        GlobalLogger.info(UserServiceImpl.class, "User with id {} updated successfully {}", id, user.toString());
        return user;

    }

    @Override
    public List<User> findAll() {
        GlobalLogger.info(UserServiceImpl.class, "Fetching all users");
        return userRepository.findAll();
    }

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