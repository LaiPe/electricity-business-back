package com.laipe.electricitybusiness.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "expected_end_date", nullable = false)
    private LocalDateTime expectedEndDate;

    @PastOrPresent
    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;

    @Digits(integer = 4, fraction = 2)
    @Column(name = "final_price", precision = 6, scale = 2, nullable = false)
    private BigDecimal finalPrice;

    @Digits(integer = 4, fraction = 2)
    @Column(name = "final_consumption_kwh", precision = 6, scale = 2, nullable = false)
    private BigDecimal finalConsumptionKwh;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "state", nullable = false)
    private BookingState state;

    @Min(1)
    @Max(5)
    @Digits(integer = 1, fraction = 0)
    @Column(name = "review_grade", columnDefinition = "TINYINT")
    private Integer reviewGrade;

    @Lob
    private String reviewComment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private ChargingStation station;
}
