package com.laipe.electricitybusiness.dto.auth;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusUserMapper extends GenericDTOMapper<User, StatusUserDTO> {
}
