package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import org.springframework.data.jpa.repository.JpaRepository;

public class BookingService extends GenericJPAService<Booking, Long> {
    public BookingService(JpaRepository<Booking, Long> repository) {
        super(repository);
    }
}
