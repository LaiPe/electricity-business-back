package com.laipe.electricitybusiness.dto.booking;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PostReviewBookingDTO {
    @Min(1)
    @Max(5)
    @Digits(integer = 1, fraction = 0)
    private Integer reviewGrade;

    private String reviewComment;
}
