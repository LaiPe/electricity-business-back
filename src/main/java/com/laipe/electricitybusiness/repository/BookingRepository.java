package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking,Long> {
}
