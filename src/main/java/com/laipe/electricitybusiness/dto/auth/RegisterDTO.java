package com.laipe.electricitybusiness.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDTO {
    @NotBlank
    @Size(min = 3, max = 200)
    @JsonProperty("username")
    private String username;

    @NotBlank
    @Size(min = 3, max = 200)
    @JsonProperty("password")
    private String password;

    @Email
    @NotBlank
    @Size(min = 2, max = 200)
    @JsonProperty("email")
    private String email;

    @NotBlank
    @Size(min = 2, max = 200)
    @JsonProperty("first_name")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 200)
    @JsonProperty("last_name")
    private String lastName;

    @NotNull
    @Past
    @JsonProperty("birth_date")
    private LocalDate birthDate;
}