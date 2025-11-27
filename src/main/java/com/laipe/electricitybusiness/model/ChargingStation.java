package com.laipe.electricitybusiness.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "charging_stations")
@Data
@NoArgsConstructor
public class ChargingStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 200)
    @Column(name = "name", length = 200,  nullable = false)
    private String name;

    @NotNull
    @Digits(integer = 3, fraction = 8)
    @Column(name = "latitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal latitude;

    @NotNull
    @Digits(integer = 3, fraction = 8)
    @Column(name = "longitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal longitude;

    @NotNull
    @Digits(integer = 2, fraction = 2)
    @Column(name = "price_per_kwh", precision = 4, scale = 2, nullable = false)
    private BigDecimal pricePerKwh;

    @NotNull
    @Digits(integer = 4, fraction = 2)
    @Column(name = "power_kw", precision = 6, scale = 2, nullable = false)
    private BigDecimal powerKw;

    @Lob
    @Column(name = "instructions")
    private String instructions;

    @PastOrPresent
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PastOrPresent
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;


    @OneToMany(mappedBy = "station")
    private List<Booking> bookings;
}
