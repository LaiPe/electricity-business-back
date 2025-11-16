package com.laipe.electricitybusiness.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Document(collection = "vehicle_models")
@Data
@NoArgsConstructor
public class VehicleModel {
    @Id
    private String id;

    @NotBlank
    @Field("make")
    @Indexed
    private String make;

    @NotBlank
    @Field("model")
    @Indexed(unique = true)
    private String model;

    @NotBlank
    @Field("year")
    @Indexed
    private String year;

    @NotNull
    @Field("consumption_kwh_per_100km")
    @JsonProperty("consumption_kwh_per_100km")
    @Min(value = 0)
    @Digits(integer = 2, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal consumptionKwhPer100Km;

    @NotNull
    @Field("battery_capacity_kwh")
    @JsonProperty("battery_capacity_kwh")
    @Min(value = 0)
    @Digits(integer = 3, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal batteryCapacityKwh;
}
