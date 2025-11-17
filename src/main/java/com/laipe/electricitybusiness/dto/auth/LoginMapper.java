package com.laipe.electricitybusiness.dto.auth;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoginMapper extends GenericDTOMapper<User, LoginDTO> {
    @Override
    @Mapping(target = "id", ignore = true)
    User toEntity(LoginDTO dto);
}
