package com.laipe.electricitybusiness.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.dto.user.PublicUserDTO;
import lombok.Data;

@Data
public class GetReviewDTO {

    @JsonProperty("booking_id")
    private Long bookingId;

    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("user")
    private PublicUserDTO user;
}
