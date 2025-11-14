package com.example.firstproject.dto;

import lombok.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String username;
    private boolean enabled;
    private Date createdAt;
    private Date updatedAt;
    private List<String> roles;
}
