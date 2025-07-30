package com.laipe.electricitybusiness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "username", length = 100,  nullable = false)
    private String username;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "password", length = 100,  nullable = false)
    private String password;

    @Email
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "email", length = 100,  nullable = false)
    private String email;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "first_name", length = 100,  nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "last_name", length = 100,  nullable = false)
    private String lastName;

    @NotNull
    @Past
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Size(min = 15,  max = 34)
    @Column(name = "iban", length = 34)
    private String iban;


    @NotNull
    @PastOrPresent
    @Column(name = "signin_date", nullable = false)
    private LocalDateTime signinDate;

    @NotNull
    @Column(name = "banned", nullable = false)
    private Boolean banned;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Vehicle> vehicles;
}
