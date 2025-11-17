package com.laipe.electricitybusiness.dto.auth;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegisterMapper extends GenericDTOMapper<User, RegisterDTO> {
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "signinDate", ignore = true)
    @Mapping(target = "banned", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "vehicles", ignore = true)
    @Mapping(target = "places", ignore = true)
    User toEntity(RegisterDTO dto);
}
