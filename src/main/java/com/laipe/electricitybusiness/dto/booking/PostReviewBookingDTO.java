package com.laipe.electricitybusiness.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PostReviewBookingDTO {
    @Min(1)
    @Max(5)
    @Digits(integer = 1, fraction = 0)
    @JsonProperty("review_grade")
    private Integer reviewGrade;

    @JsonProperty("review_comment")
    private String reviewComment;
}
