package com.laipe.electricitybusiness.dto.user;

import com.laipe.electricitybusiness.dto.GenericDTOMapper;
import com.laipe.electricitybusiness.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UpdateUsernameMapper extends GenericDTOMapper<User, UpdateUsernameDTO> {
}
