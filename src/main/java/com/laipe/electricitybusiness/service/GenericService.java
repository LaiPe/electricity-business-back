package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.util.ModelUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public abstract class GenericService<T, ID> {

    private final JpaRepository<T, ID> repository;


    @Transactional
    public T create(T entity) {
        return repository.save(entity);
    }

    public List<T> getAll() {
        return repository.findAll();
    }

    public Optional<T> getById(ID id) {
        return repository.findById(id);
    }

    @Transactional
    public Optional<T> deleteById(ID id) {
        Optional<T> entity = repository.findById(id);
        entity.ifPresent(repository::delete);
        return entity;
    }

    @Transactional
    public Optional<T> update(T newEntity, ID id) {
        return repository.findById(id)
                .map(existingEntity -> {
                    ModelUtil.copyFields(newEntity, existingEntity);
                    return repository.save(existingEntity);
                });
    }
}