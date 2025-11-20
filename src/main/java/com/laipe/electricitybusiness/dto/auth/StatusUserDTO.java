package com.laipe.electricitybusiness.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.model.UserRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Data
public class StatusUserDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("banned")
    private Boolean banned;

    @JsonProperty("verified")
    private Boolean verified;

    @JsonProperty("role")
    private UserRole role;

    public Collection<? extends GrantedAuthority> giveAuthorities() {
        return List.of(role);
    }
}
