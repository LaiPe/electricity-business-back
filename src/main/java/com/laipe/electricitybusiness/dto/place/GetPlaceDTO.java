package com.laipe.electricitybusiness.dto.place;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laipe.electricitybusiness.dto.chargingstations.GetChargingStationDTO;
import lombok.Data;

import java.util.List;

@Data
public class GetPlaceDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty("charging_stations")
    private List<GetChargingStationDTO> chargingStations;
}
