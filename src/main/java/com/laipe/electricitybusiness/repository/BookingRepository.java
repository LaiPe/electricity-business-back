package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query("SELECT b FROM Booking b WHERE b.vehicle.owner.id = :ownerId")
    List<Booking> findAllByVehicleOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.station.place.owner.id = :ownerId")
    List<Booking> findAllByStationOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.station.id = :stationId")
    List<Booking> findAllByStationId(@Param("stationId") Long stationId);
}
