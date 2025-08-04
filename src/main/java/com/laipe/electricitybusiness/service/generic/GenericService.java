package com.laipe.electricitybusiness.service.generic;

import java.util.List;
import java.util.Optional;

public interface GenericService<T, ID> {
        T create(T entity);

        List<T> getAll();

        Optional<T> getById(ID id);

        Optional<T> deleteById(ID id);

        Optional<T> update(T newEntity, ID id);
}
