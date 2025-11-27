package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.owner.id = :ownerId AND v.deletedAt IS NULL")
    List<Vehicle> findAllNotDeletedByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT v FROM Vehicle v WHERE v.owner.id = :ownerId AND v.deletedAt IS NOT NULL")
    List<Vehicle> findAllDeletedByOwnerId(@Param("ownerId")Long ownerId);

    @Query("SELECT v FROM Vehicle v WHERE v.deletedAt IS NULL")
    List<Vehicle> findAllNotDeleted();

    @Query("SELECT v FROM Place v WHERE v.deletedAt IS NOT NULL")
    List<Vehicle> findAllDeleted();
}
