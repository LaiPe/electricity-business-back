package com.laipe.electricitybusiness.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.LocalDate;

@Document(collection = "vehicle_model")
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
    @Field("basemodel")
    @Indexed
    private String baseModel;

    @NotBlank
    @Field("year")
    @Indexed
    private String year;

    @NotNull
    @Field("cons_kwh_per_100km")
    @Min(value = 0)
    @Digits(integer = 2, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal consKwhPer100Km;

    @NotNull
    @Field("range")
    @Min(value = 0)
    private Integer range;

    @NotNull
    @Field("time_charge240")
    @Min(value = 0)
    @Digits(integer = 2, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal timeCharge240;

    @NotNull
    @Field("time_charge240b")
    @Min(value = 0)
    @Digits(integer = 2, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private BigDecimal timeCharge240b;

    @Field("c240dscr")
    private String c240dscr;

    @Field("c240bdscr")
    private String c240bdscr;

    @NotNull
    @Field("modifiedon")
    private LocalDate modifiedOn;
}
