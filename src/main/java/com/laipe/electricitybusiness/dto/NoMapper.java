package com.laipe.electricitybusiness.dto;

public class NoMapper<T> implements GenericDTOMapper<T, T>{
    @Override
    public T toDto(T entity) {
        return entity;
    }

    @Override
    public T toEntity(T entity) {
        return entity;
    }
}
