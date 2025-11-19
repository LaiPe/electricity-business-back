package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.InvalidBookingState;
import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.model.BookingState;
import com.laipe.electricitybusiness.repository.BookingRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import com.laipe.electricitybusiness.utils.ModelUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService extends GenericJPAService<Booking, Long> {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        super(bookingRepository);
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Booking create(Booking entity) {
        entity.setState(BookingState.PENDING_ACCEPT);
        return super.create(entity);
    }

    public List<Booking> getAllByVehicleOwnerId(Long ownerId) {
        return bookingRepository.findAllByVehicleOwnerId(ownerId);
    }

    public List<Booking> getAllByStationOwnerId(Long ownerId) {
        return bookingRepository.findAllByStationOwnerId(ownerId);
    }

    public Optional<Booking> acceptBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.ACCEPTED);

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.PENDING_ACCEPT) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in PENDING_ACCEPT state.");
                    }
                });
    }

    public Optional<Booking> rejectBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.REJECTED);

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.PENDING_ACCEPT) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in PENDING_ACCEPT state.");
                    }
                });
    }

    public Optional<Booking> cancelBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.CANCELLED);

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.ACCEPTED) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in ACCEPTED state.");
                    }
                });
    }

    public Optional<Booking> startBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.ONGOING);

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.ACCEPTED) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in ACCEPTED state.");
                    }
                });
    }

    public Optional<Booking> endBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.COMPLETED);
        updatedBooking.setActualEndDate(LocalDateTime.now());
        // TODO : calculate total cost
        // TODO : calculate consumed energy

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.ONGOING) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in ONGOING state.");
                    }
                });
    }

    public Optional<Booking> reviewBooking(Long id, Integer grade, String comment) {
        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.COMPLETED) {
                        existingBooking.setReviewGrade(grade);
                        existingBooking.setReviewComment(comment);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in COMPLETED state.");
                    }
                });
    }
}
