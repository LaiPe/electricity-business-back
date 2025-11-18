package com.laipe.electricitybusiness.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.model.UserRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PostUserDTO {
    @NotBlank
    @Size(min = 2, max = 100)
    @JsonProperty("username")
    private String username;

    @NotBlank
    @Size(min = 2, max = 100)
    @JsonProperty("password")
    private String password;

    @Email
    @NotBlank
    @JsonProperty("email")
    private String email;

    @NotBlank
    @Size(min = 2, max = 100)
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 100)
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @Past
    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    @JsonProperty("role")
    private UserRole role;
}
