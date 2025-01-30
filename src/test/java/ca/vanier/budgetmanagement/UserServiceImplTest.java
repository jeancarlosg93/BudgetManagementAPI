package ca.vanier.budgetmanagement;

import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.repositories.UserRepository;
import ca.vanier.budgetmanagement.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User(
                "testuser",
                "password123",
                User.ROLE_USER,
                "Test",
                "User",
                "test@example.com",
                "123-456-7890"
        );

        User adminUser = new User(
                "adminuser",
                "adminpass",
                User.ROLE_ADMIN,
                "Admin",
                "User",
                "admin@example.com",
                "098-765-4321"
        );

        testUsers = Arrays.asList(testUser, adminUser);
    }

    @Test
    void whenSaveUser_thenPasswordIsEncoded() {
        // Arrange
        testUser.setId(null); // Ensure ID is null for new user
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User savedUser = userService.save(testUser);

        // Assert
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
        assertNotNull(savedUser);
    }

    @Test
    void whenSaveUserWithExistingUsername_thenThrowException() {
        // Arrange
        testUser.setId(null);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.save(testUser);
        });
        assertEquals("User already exists with username: " + testUser.getUsername(), exception.getMessage());
    }

    @Test
    void whenSaveUserWithExistingId_thenThrowException() {
        // Arrange
        testUser.setId(1L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.save(testUser);
        });
        assertEquals("Cannot save user with existing ID", exception.getMessage());
    }

    @Test
    void whenSaveAllUsers_thenAllPasswordsAreEncoded() {
        // Arrange
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.saveAll(anyList())).thenReturn(testUsers);

        // Act
        userService.saveAll(testUsers);

        // Assert
        verify(passwordEncoder, times(testUsers.size())).encode(any());
        verify(userRepository).saveAll(testUsers);
    }

    @Test
    void whenFindById_thenReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> found = userService.findById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(testUser, found.get());
    }

    @Test
    void whenFindByUsername_thenReturnUser() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> found = userService.findByUsername("testuser");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void whenFindAll_thenReturnUserList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(testUsers);

        // Act
        List<User> found = userService.findAll();

        // Assert
        assertFalse(found.isEmpty());
        assertEquals(2, found.size());
    }

    @Test
    void whenUpdateExistingUser_thenReturnUpdatedUser() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User(
                "oldusername",
                "oldpassword",
                User.ROLE_USER,
                "Old",
                "User",
                "old@example.com",
                "111-111-1111"
        );
        existingUser.setId(userId);

        User userDetails = new User(
                "newusername",
                "newpassword",
                User.ROLE_USER,
                "New",
                "User",
                "new@example.com",
                "222-222-2222"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        User updatedUser = userService.updateExistingUser(userId, userDetails);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals(userDetails.getUsername(), updatedUser.getUsername());
        assertEquals(userDetails.getFirstName(), updatedUser.getFirstName());
        assertEquals(userDetails.getLastName(), updatedUser.getLastName());
        assertEquals(userDetails.getEmail(), updatedUser.getEmail());
        assertEquals(userDetails.getPhone(), updatedUser.getPhone());
    }

    @Test
    void whenDeleteExistingUser_thenDeleteSuccessfully() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void whenDeleteNonExistingUser_thenThrowException() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }
}