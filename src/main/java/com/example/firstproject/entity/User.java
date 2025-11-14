package com.example.firstproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "username", nullable = false, length = 50)
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username must be at least 3 characters")
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100, message = "password must be at least 6 characters")
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @CreationTimestamp //fills timestamps automatically.
    @Temporal(TemporalType.TIMESTAMP)//how to map a Java Date
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}
