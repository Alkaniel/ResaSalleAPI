package fr.enzogiardinelli.rsa.api.user.controller;

import fr.enzogiardinelli.rsa.api.user.model.Role;
import fr.enzogiardinelli.rsa.api.user.model.User;
import fr.enzogiardinelli.rsa.api.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record UserDTO(String firstName, String lastName, String email, String password, Role role) {}

    @PostMapping
    @PreAuthorize("hasAnyAuthority('MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody UserDTO request) {
        Optional<User> existingUser = userRepository.findByEmail(request.email());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(409).build();
        }

        User newUser = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role() : Role.USER)
                .build();

        newUser.setActive(true);
        return ResponseEntity.ok(userRepository.save(newUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @RequestBody UserDTO request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        User existingUser = optionalUser.get();
        if (request.firstName() != null) existingUser.setFirstName(request.firstName());
        if (request.lastName() != null) existingUser.setLastName(request.lastName());
        if (request.email() != null) existingUser.setEmail(request.email());
        if (request.role() != null) existingUser.setRole(request.role());

        if (request.password() != null && !request.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        return ResponseEntity.ok(userRepository.save(existingUser));
    }
}