package com.laipe.electricitybusiness.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StrictUserDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;
}
