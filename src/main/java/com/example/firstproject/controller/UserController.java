package com.example.firstproject.controller;

import com.example.firstproject.dto.UserDTO;
import com.example.firstproject.entity.User;
import com.example.firstproject.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {


    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/users")
    public List<UserDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/users/{username}")
    public UserDTO getUser(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @PostMapping("/users")
    public UserDTO addUser(@Valid @RequestBody User user,
                           @RequestParam(required = false) List<String> roles) {
        return userService.save(user, roles);
    }

    @PutMapping("/users/{username}")
    public UserDTO updateUser(@PathVariable String username,
                              @Valid @RequestBody User updatedUser,
                              @RequestParam(required = false) List<String> roles) {
        return userService.update(username, updatedUser, roles);
    }

    @DeleteMapping("/users/{username}")
    public String delete(@PathVariable String username) {
        userService.deleteByUsername(username);
        return "Deleted user - " + username;
    }
}
