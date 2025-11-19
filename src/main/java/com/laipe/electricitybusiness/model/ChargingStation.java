package com.laipe.electricitybusiness.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;


    @OneToMany(mappedBy = "station")
    private List<Booking> bookings;
}
