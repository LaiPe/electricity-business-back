package com.laipe.electricitybusiness.dto;

import com.laipe.electricitybusiness.model.UserRole;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private UserRole role;
    private LocalDateTime signinDate;
    private Boolean banned;
}
