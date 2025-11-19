package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User u = new User();
        u.setUsername("booker");
        u.setPassword("pwd");
        u.setEmail("booker@example.com");
        u.setFirstName("Booker");
        u.setLastName("User");
        u.setBirthDate(LocalDate.of(1992,4,4));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(false);
        u.setVerificationCode("456789");
        u.setCodeExpirationDate(LocalDateTime.now().plusHours(24));
        return u;
    }

    private Place createPlace(User owner) {
        Place p = new Place();
        p.setName("Parking Sud");
        p.setOwner(owner);
        return p;
    }

    private ChargingStation createStation(Place place) {
        ChargingStation s = new ChargingStation();
        s.setName("Borne A");
        s.setLatitude(new BigDecimal("48.0"));
        s.setLongitude(new BigDecimal("2.0"));
        s.setPricePerKwh(new BigDecimal("0.30"));
        s.setPowerKw(new BigDecimal("11.00"));
        s.setPlace(place);
        return s;
    }

    private Vehicle createVehicle(User owner) {
        Vehicle v = new Vehicle();
        v.setRegistrationNumber("ZZ-999");
        v.setOwner(owner);
        v.setModelId("m-1");
        return v;
    }

    private Booking createBooking(Vehicle vehicle, ChargingStation station) {
        Booking b = new Booking();
        b.setStartDate(LocalDateTime.now().plusDays(1));
        b.setExpectedEndDate(LocalDateTime.now().plusDays(1).plusHours(2));
        b.setFinalPrice(new BigDecimal("5.00"));
        b.setFinalConsumptionKwh(new BigDecimal("10.00"));
        b.setState(BookingState.PENDING_ACCEPT);
        b.setVehicle(vehicle);
        b.setStation(station);
        return b;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User owner = userRepository.save(createUser());
        Place place = placeRepository.save(createPlace(owner));
        ChargingStation station = chargingStationRepository.save(createStation(place));
        Vehicle vehicle = vehicleRepository.save(createVehicle(owner));

        Booking saved = bookingRepository.save(createBooking(vehicle, station));
        assertThat(saved.getId()).isNotNull();

        List<Booking> all = bookingRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<Booking> found = bookingRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getState()).isEqualTo(BookingState.PENDING_ACCEPT);

        bookingRepository.delete(found.get());
        assertThat(bookingRepository.findById(saved.getId())).isEmpty();
    }
}

