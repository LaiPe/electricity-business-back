package com.laipe.electricitybusiness.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 15)
    @Column(name = "registration_number", length = 15, nullable = false, unique = true)
    private String registrationNumber;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotBlank
    @Column(name = "model_id", nullable = false)
    private String modelId;


    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
}
