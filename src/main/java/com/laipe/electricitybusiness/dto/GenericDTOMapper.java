package com.laipe.electricitybusiness.dto;

public interface GenericDTOMapper<T, DTO> {
    DTO toDto(T entity);
    T toEntity(DTO dto);
}
