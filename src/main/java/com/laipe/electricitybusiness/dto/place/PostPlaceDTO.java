package com.laipe.electricitybusiness.dto.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostPlaceDTO {
    @NotBlank
    @Size(min = 2, max = 200)
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}
