package com.example.support_center_backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Column(unique = true)
    private String phoneNumber;
    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;
    @OneToOne(mappedBy = "user")
    private Otp otp;
}
