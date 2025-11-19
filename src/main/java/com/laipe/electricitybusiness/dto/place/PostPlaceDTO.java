package com.laipe.electricitybusiness.dto.place;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostPlaceDTO {
    @NotBlank
    @Size(min = 2, max = 200)
    private String name;

    private String description;
}
