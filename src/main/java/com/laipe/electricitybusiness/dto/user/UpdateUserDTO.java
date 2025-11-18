package com.laipe.electricitybusiness.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserDTO {
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
