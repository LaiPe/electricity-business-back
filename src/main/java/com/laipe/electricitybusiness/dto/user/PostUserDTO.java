package com.laipe.electricitybusiness.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.model.UserRole;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PostUserDTO {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("email")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @JsonProperty("role")
    private UserRole role;

    @JsonProperty("iban")
    private String iban;
}
