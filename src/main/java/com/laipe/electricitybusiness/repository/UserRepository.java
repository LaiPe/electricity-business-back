package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN Booking b ON b.station.place.owner.id = u.id WHERE b.id = :bookingId")
    Optional<User> findStationOwnerBookingById(@Param("bookingId") Long bookingId);

    @Query("SELECT u FROM User u JOIN Booking b ON b.vehicle.owner.id = u.id WHERE b.id = :bookingId")
    Optional<User> findVehicleOwnerBookingById(@Param("bookingId") Long bookingId);
}
