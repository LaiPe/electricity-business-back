package com.laipe.electricitybusiness.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank
    @Size(min = 3, max = 200)
    private String username;

    @NotBlank
    @Size(min = 3, max = 200)
    private String password;
}
