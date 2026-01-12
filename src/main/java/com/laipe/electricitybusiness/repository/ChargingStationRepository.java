package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChargingStationRepository extends JpaRepository<ChargingStation,Long> {

    @Query("SELECT DISTINCT cs FROM ChargingStation cs " +
           "LEFT JOIN FETCH cs.bookings b " +
           "LEFT JOIN FETCH b.vehicle v " +
           "LEFT JOIN FETCH v.owner " +
           "WHERE cs.deletedAt IS NULL")
    List<ChargingStation> findAllNotDeletedWithBookings();

    @Query("SELECT cs FROM ChargingStation cs WHERE cs.deletedAt IS NULL")
    List<ChargingStation> findAllNotDeleted();

    @Query("SELECT cs FROM ChargingStation cs WHERE cs.deletedAt IS NOT NULL")
    List<ChargingStation> findAllDeleted();

    @Query("SELECT cs FROM ChargingStation cs WHERE cs.place.id = :placeId AND cs.deletedAt IS NULL")
    List<ChargingStation> findAllByPlaceIdAndDeletedAtIsNull(@Param("placeId") Long placeId);
}
