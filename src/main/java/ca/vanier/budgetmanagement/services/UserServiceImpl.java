package ca.vanier.budgetmanagement.services;

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

        if (user.getId() != null) {
            throw new IllegalArgumentException("User cannot be null or empty");
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateExistingUser(Long id, User userDetails) {

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return userRepository.save(user);

    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}