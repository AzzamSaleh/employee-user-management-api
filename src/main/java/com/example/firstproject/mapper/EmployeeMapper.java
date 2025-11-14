package com.example.firstproject.mapper;

import com.example.firstproject.dto.EmployeeDTO;
import com.example.firstproject.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDTO toDTO(Employee e) {
        if (e == null) return null;
        return EmployeeDTO.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
