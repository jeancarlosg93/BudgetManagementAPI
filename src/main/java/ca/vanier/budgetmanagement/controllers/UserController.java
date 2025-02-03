package ca.vanier.budgetmanagement.controllers;

import ca.vanier.budgetmanagement.entities.User;
import ca.vanier.budgetmanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            List<User> users = userService.findAll();
            if (users.isEmpty()) {
                return error("find.no.results", new Object[]{}, locale);
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return error("find.error", null, locale);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id,
                                         @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            return ResponseEntity.ok(userService.findById(id));
        } catch (IllegalArgumentException e) {
            return error("user.error.not.found", new Object[]{id}, locale);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody User user,
                                      @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            User savedUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (IllegalArgumentException e) {
            return error("user.error.save", null, locale);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails,
                                        @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            User updatedUser = userService.updateExistingUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return error("user.error.not.found", new Object[]{id}, locale);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id,
                                             @RequestHeader(name = "Accept-Language", required = false) Locale locale) {
        try {
            userService.deleteUser(id);
            return success("user.deleted", locale);
        } catch (IllegalArgumentException e) {
            return error("user.error.delete", null, locale);
        }
    }
}