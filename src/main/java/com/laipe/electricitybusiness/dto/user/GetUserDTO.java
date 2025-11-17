package com.laipe.electricitybusiness.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.model.UserRole;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GetUserDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

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

    @JsonProperty("signin_date")
    private LocalDateTime signinDate;

    @JsonProperty("banned")
    private Boolean banned;

    @JsonProperty("verified")
    private Boolean verified;
}
