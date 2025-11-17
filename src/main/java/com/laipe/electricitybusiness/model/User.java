package com.laipe.electricitybusiness.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 200)
    @Column(name = "username", length = 200,  nullable = false)
    private String username;

    @NotBlank
    @Size(min = 2, max = 200)
    @Column(name = "password", length = 200,  nullable = false)
    private String password;

    @Email
    @NotBlank
    @Size(min = 2, max = 200)
    @Column(name = "email", length = 200,  nullable = false)
    private String email;

    @NotBlank
    @Size(min = 2, max = 200)
    @Column(name = "first_name", length = 200,  nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 200)
    @Column(name = "last_name", length = 200,  nullable = false)
    private String lastName;

    @NotNull
    @Past
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "role", nullable = false)
    private UserRole role;

    @NotNull
    @PastOrPresent
    @Column(name = "signin_date", nullable = false)
    private LocalDateTime signinDate;

    @NotNull
    @Column(name = "banned", nullable = false)
    private Boolean banned;

    @NotNull
    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @NotNull
    @Column(name = "verification_code", nullable = false)
    private String verificationCode;

    @NotNull
    @Column(name = "code_expiration_date", nullable = false)
    private LocalDateTime codeExpirationDate;




    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Place> places;




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }
}
