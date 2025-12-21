package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.IntegrityConstraintViolationException;
import com.laipe.electricitybusiness.controller.handler.InvalidBookingState;
import com.laipe.electricitybusiness.model.*;
import com.laipe.electricitybusiness.repository.BookingRepository;
import com.laipe.electricitybusiness.repository.UserRepository;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import com.laipe.electricitybusiness.repository.VehicleRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleService extends GenericJPAService<Vehicle, Long> {

    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository, VehicleModelRepository vehicleModelRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        super(vehicleRepository);
        this.vehicleRepository = vehicleRepository;
        this.vehicleModelRepository = vehicleModelRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Vehicle create(Vehicle entity) {
        userRepository.findById(entity.getOwner().getId())
                .filter(e -> e.getDeletedAt() != null) // Filter undeleted user
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Cannot create a vehicle for a deleted user.");
                });

        vehicleModelRepository.findById(entity.getModelId())
            .orElseThrow(() -> new IntegrityConstraintViolationException("vehicleModelId", entity.getModelId(), VehicleModel.class));

        entity.setCreatedAt(LocalDateTime.now());
        return super.create(entity);
    }

    @Override
    public Optional<Vehicle> update(Vehicle entity, Long id) {
        userRepository.findById(entity.getOwner().getId())
                .filter(e -> e.getDeletedAt() != null) // Filter undeleted user
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Cannot update a vehicle for a deleted user.");
                });

        vehicleModelRepository.findById(entity.getModelId())
                .orElseThrow(() -> new IntegrityConstraintViolationException("vehicleModelId", entity.getModelId(), VehicleModel.class));

        return super.update(entity, id);
    }

    public List<Vehicle> getAllByOwnerId(Long ownerId) {
        return vehicleRepository.findAllNotDeletedByOwnerId(ownerId);
    }

    @Override
    public List<Vehicle> getAll() {
        return vehicleRepository.findAllNotDeleted();
    }

    @Override
    public Optional<Vehicle> deleteById(Long id) {
        // First, handle bookings associated with the vehicle
        List<Booking> bookings = bookingRepository.findAllByVehiculeId(id);
        bookings.forEach(booking -> {
            // If there's an ongoing booking, we cannot delete the vehicle
            if (booking.getState() == BookingState.ONGOING) {
                throw new InvalidBookingState("Cannot delete vehicle with ongoing bookings.");
            }
            // For pending or accepted bookings, we cancel them
            else if (booking.getState() == BookingState.PENDING_ACCEPT || booking.getState() == BookingState.ACCEPTED) {
                booking.setState(BookingState.CANCELLED);
            }
        });
        bookingRepository.saveAll(bookings);

        return vehicleRepository.findById(id)
                .map(station -> {
                    station.setDeletedAt(LocalDateTime.now());
                    return vehicleRepository.save(station);
                });
    }
}
