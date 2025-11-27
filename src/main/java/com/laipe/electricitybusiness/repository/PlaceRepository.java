package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place,Long> {
    @Query("SELECT p FROM Place p WHERE p.owner.id = :ownerId AND p.deletedAt IS NULL")
    List<Place> findAllNotDeletedByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT p FROM Place p WHERE p.owner.id = :ownerId AND p.deletedAt IS NOT NULL")
    List<Place> findAllDeletedByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT p FROM Place p WHERE p.deletedAt IS NULL")
    List<Place> findAllNotDeleted();

    @Query("SELECT p FROM Place p WHERE p.deletedAt IS NOT NULL")
    List<Place> findAllDeleted();
}
