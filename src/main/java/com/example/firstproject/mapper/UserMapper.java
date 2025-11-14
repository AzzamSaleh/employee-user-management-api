package com.example.firstproject.mapper;

import com.example.firstproject.dto.UserDTO;
import com.example.firstproject.entity.User;

import java.util.List;

public class UserMapper {

    public static UserDTO toDTO(User user, List<String> roles) {
        if (user == null) return null;
        return UserDTO.builder()
                .username(user.getUsername())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(roles)
                .build();
    }
}
