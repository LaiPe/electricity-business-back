package com.laipe.electricitybusiness.dto.chargingstations;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.ChargingStation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UpdateChargingStationMapper extends GenericDTOMapper<ChargingStation, UpdateChargingStationDTO> {
}
