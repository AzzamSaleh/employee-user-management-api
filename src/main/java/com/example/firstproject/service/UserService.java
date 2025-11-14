package com.example.firstproject.service;

import com.example.firstproject.dto.UserDTO;
import com.example.firstproject.entity.Authority;
import com.example.firstproject.entity.User;
import com.example.firstproject.exception.UserAlreadyExistsException;
import com.example.firstproject.exception.UserNotFoundException;
import com.example.firstproject.mapper.UserMapper;
import com.example.firstproject.repository.AuthorityRepository;
import com.example.firstproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository,
                       AuthorityRepository authorityRepository,
                       PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.encoder = encoder;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(u -> UserMapper.toDTO(u, getRoles(u.getUsername())))
                .collect(Collectors.toList());
    }

    public UserDTO findByUsername(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username - " + username));
        return UserMapper.toDTO(user, getRoles(username));
    }

    // Create new user (throws if exists)
    public UserDTO save(User user, List<String> roles) {
        String username = user.getUsername().trim();

        if (userRepository.existsById(username)) {
            throw new UserAlreadyExistsException("User already exists: " + username);
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(true);
        User saved = userRepository.save(user);

        // Save roles
        if (roles != null && !roles.isEmpty()) {
            for (String role : roles) {
                if (role == null || role.isBlank()) continue;
                authorityRepository.save(
                        Authority.builder()
                                .username(username)
                                .authority(role.startsWith("ROLE_") ? role : "ROLE_" + role)
                                .build()
                );
            }
        }

        return UserMapper.toDTO(saved, getRoles(username));
    }

    // Update existing user
    public UserDTO update(String username, User updatedUser, List<String> roles) {
        User existingUser = userRepository.findById(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(encoder.encode(updatedUser.getPassword()));
        }
        // enabled defaults to false if not provided on request body; keep current if null isn't possible.
        existingUser.setEnabled(updatedUser.isEnabled());
        User saved = userRepository.save(existingUser);

        // Update roles
        if (roles != null) {
            // Remove old roles first
            authorityRepository.findByUsername(username)
                    .forEach(a -> authorityRepository.deleteById(a.getId()));

            // Add new roles
            for (String role : roles) {
                if (role == null || role.isBlank()) continue;
                authorityRepository.save(
                        Authority.builder()
                                .username(username)
                                .authority(role.startsWith("ROLE_") ? role : "ROLE_" + role)
                                .build()
                );
            }
        }

        return UserMapper.toDTO(saved, getRoles(username));
    }

    // Delete user (delete roles first)
    public void deleteByUsername(String username) {
        if (!userRepository.existsById(username)) {
            throw new UserNotFoundException("User not found - " + username);
        }

        authorityRepository.findByUsername(username)
                .forEach(a -> authorityRepository.deleteById(a.getId()));

        userRepository.deleteById(username);
    }

    private List<String> getRoles(String username) {
        return authorityRepository.findByUsername(username)
                .stream()
                .map(Authority::getAuthority)
                .collect(Collectors.toList());
    }
}
