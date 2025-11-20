package com.laipe.electricitybusiness.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank
    @Size(min = 3, max = 200)
    @JsonProperty("username")
    private String username;

    @NotBlank
    @Size(min = 3, max = 200)
    @JsonProperty("password")
    private String password;
}
