package com.laipe.electricitybusiness.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatusUserDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("banned")
    private Boolean banned;

    @JsonProperty("verified")
    private Boolean verified;
}
