package com.laipe.electricitybusiness.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateEmailDTO {
    @Email
    @NotBlank
    @Size(min = 2, max = 200)
    @JsonProperty("email")
    private String email;
}
