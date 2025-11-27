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

    private User createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@example.com");
        u.setFirstName("Booker");
        u.setLastName("User");
        u.setBirthDate(LocalDate.of(1992, 4, 4));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(false);
        u.setVerificationCode("456789");
        u.setCodeExpirationDate(LocalDateTime.now().plusHours(24));
        return u;
    }

    private Place createPlace(User owner, String name) {
        Place p = new Place();
        p.setName(name);
        p.setDescription("Description de " + name);
        p.setOwner(owner);
        p.setCreatedAt(LocalDateTime.now());
        return p;
    }

    private ChargingStation createStation(Place place, String name) {
        ChargingStation s = new ChargingStation();
        s.setName(name);
        s.setLatitude(new BigDecimal("48.0"));
        s.setLongitude(new BigDecimal("2.0"));
        s.setPricePerKwh(new BigDecimal("0.30"));
        s.setPowerKw(new BigDecimal("11.00"));
        s.setPlace(place);
        s.setCreatedAt(LocalDateTime.now());
        return s;
    }

    private Vehicle createVehicle(User owner, String regNumber) {
        Vehicle v = new Vehicle();
        v.setRegistrationNumber(regNumber);
        v.setOwner(owner);
        v.setModelId("m-1");
        v.setCreatedAt(LocalDateTime.now());
        return v;
    }

    private Booking createBooking(Vehicle vehicle, ChargingStation station, BookingState state) {
        Booking b = new Booking();
        b.setStartDate(LocalDateTime.now().plusDays(1));
        b.setExpectedEndDate(LocalDateTime.now().plusDays(1).plusHours(2));
        b.setFinalPrice(new BigDecimal("5.00"));
        b.setFinalConsumptionKwh(new BigDecimal("10.00"));
        b.setState(state);
        b.setVehicle(vehicle);
        b.setStation(station);
        return b;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User owner = userRepository.save(createUser("booker"));
        Place place = placeRepository.save(createPlace(owner, "Parking Sud"));
        ChargingStation station = chargingStationRepository.save(createStation(place, "Borne A"));
        Vehicle vehicle = vehicleRepository.save(createVehicle(owner, "ZZ-999"));

        Booking saved = bookingRepository.save(createBooking(vehicle, station, BookingState.PENDING_ACCEPT));
        assertThat(saved.getId()).isNotNull();

        List<Booking> all = bookingRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<Booking> found = bookingRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getState()).isEqualTo(BookingState.PENDING_ACCEPT);

        bookingRepository.delete(found.get());
        assertThat(bookingRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testFindAllByVehicleOwnerId() {
        User vehicleOwner = userRepository.save(createUser("vehicleowner"));
        User stationOwner = userRepository.save(createUser("stationowner"));

        Place place = placeRepository.save(createPlace(stationOwner, "Parking Test"));
        ChargingStation station = chargingStationRepository.save(createStation(place, "Borne Test"));

        Vehicle vehicle1 = vehicleRepository.save(createVehicle(vehicleOwner, "AA-111"));
        Vehicle vehicle2 = vehicleRepository.save(createVehicle(vehicleOwner, "BB-222"));
        Vehicle otherVehicle = vehicleRepository.save(createVehicle(stationOwner, "CC-333"));

        bookingRepository.save(createBooking(vehicle1, station, BookingState.PENDING_ACCEPT));
        bookingRepository.save(createBooking(vehicle2, station, BookingState.ACCEPTED));
        bookingRepository.save(createBooking(otherVehicle, station, BookingState.PENDING_ACCEPT));

        List<Booking> bookings = bookingRepository.findAllByVehicleOwnerId(vehicleOwner.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings).allMatch(b -> b.getVehicle().getOwner().getId().equals(vehicleOwner.getId()));
    }

    @Test
    void testFindAllByStationOwnerId() {
        User stationOwner = userRepository.save(createUser("stationowner2"));
        User vehicleOwner = userRepository.save(createUser("vehicleowner2"));

        Place place1 = placeRepository.save(createPlace(stationOwner, "Parking 1"));
        Place place2 = placeRepository.save(createPlace(stationOwner, "Parking 2"));

        ChargingStation station1 = chargingStationRepository.save(createStation(place1, "Borne 1"));
        ChargingStation station2 = chargingStationRepository.save(createStation(place2, "Borne 2"));

        Vehicle vehicle = vehicleRepository.save(createVehicle(vehicleOwner, "DD-444"));

        bookingRepository.save(createBooking(vehicle, station1, BookingState.ACCEPTED));
        bookingRepository.save(createBooking(vehicle, station2, BookingState.ONGOING));

        List<Booking> bookings = bookingRepository.findAllByStationOwnerId(stationOwner.getId());

        assertThat(bookings).hasSize(2);
        assertThat(bookings).allMatch(b -> b.getStation().getPlace().getOwner().getId().equals(stationOwner.getId()));
    }

    @Test
    void testFindAllByStationId() {
        User owner = userRepository.save(createUser("multiowner"));
        Place place = placeRepository.save(createPlace(owner, "Parking Multi"));

        ChargingStation station1 = chargingStationRepository.save(createStation(place, "Borne X"));
        ChargingStation station2 = chargingStationRepository.save(createStation(place, "Borne Y"));

        Vehicle vehicle = vehicleRepository.save(createVehicle(owner, "EE-555"));

        bookingRepository.save(createBooking(vehicle, station1, BookingState.PENDING_ACCEPT));
        bookingRepository.save(createBooking(vehicle, station1, BookingState.ACCEPTED));
        bookingRepository.save(createBooking(vehicle, station1, BookingState.COMPLETED));
        bookingRepository.save(createBooking(vehicle, station2, BookingState.ACCEPTED));

        List<Booking> bookings = bookingRepository.findAllByStationId(station1.getId());

        assertThat(bookings).hasSize(3);
        assertThat(bookings).allMatch(b -> b.getStation().getId().equals(station1.getId()));
    }

    @Test
    void testBookingWithSoftDeletedVehicle() {
        User owner = userRepository.save(createUser("testowner1"));
        Place place = placeRepository.save(createPlace(owner, "Parking Test 1"));
        ChargingStation station = chargingStationRepository.save(createStation(place, "Borne Test 1"));
        Vehicle vehicle = vehicleRepository.save(createVehicle(owner, "FF-666"));

        Booking booking = bookingRepository.save(createBooking(vehicle, station, BookingState.ACCEPTED));

        // Suppression logique du véhicule
        vehicle.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);

        // La réservation doit toujours exister et référencer le véhicule
        Optional<Booking> foundBooking = bookingRepository.findById(booking.getId());
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get().getVehicle()).isNotNull();
        assertThat(foundBooking.get().getVehicle().getDeletedAt()).isNotNull();
    }

    @Test
    void testBookingWithSoftDeletedStation() {
        User owner = userRepository.save(createUser("testowner2"));
        Place place = placeRepository.save(createPlace(owner, "Parking Test 2"));
        ChargingStation station = chargingStationRepository.save(createStation(place, "Borne Test 2"));
        Vehicle vehicle = vehicleRepository.save(createVehicle(owner, "GG-777"));

        Booking booking = bookingRepository.save(createBooking(vehicle, station, BookingState.ONGOING));

        // Suppression logique de la borne
        station.setDeletedAt(LocalDateTime.now());
        chargingStationRepository.save(station);

        // La réservation doit toujours exister et référencer la borne
        Optional<Booking> foundBooking = bookingRepository.findById(booking.getId());
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get().getStation()).isNotNull();
        assertThat(foundBooking.get().getStation().getDeletedAt()).isNotNull();
    }

    @Test
    void testFindBookingsWithDeletedResources() {
        User vehicleOwner = userRepository.save(createUser("deletedtest1"));
        User stationOwner = userRepository.save(createUser("deletedtest2"));

        Place place = placeRepository.save(createPlace(stationOwner, "Parking Archived"));
        ChargingStation station = chargingStationRepository.save(createStation(place, "Borne Archived"));
        Vehicle vehicle = vehicleRepository.save(createVehicle(vehicleOwner, "HH-888"));

        Booking booking1 = bookingRepository.save(createBooking(vehicle, station, BookingState.COMPLETED));
        Booking booking2 = bookingRepository.save(createBooking(vehicle, station, BookingState.ACCEPTED));

        // Suppression logique du véhicule et de la borne
        vehicle.setDeletedAt(LocalDateTime.now());
        station.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
        chargingStationRepository.save(station);

        // Les réservations doivent toujours être trouvées
        List<Booking> vehicleBookings = bookingRepository.findAllByVehicleOwnerId(vehicleOwner.getId());
        assertThat(vehicleBookings).hasSize(2);

        List<Booking> stationBookings = bookingRepository.findAllByStationOwnerId(stationOwner.getId());
        assertThat(stationBookings).hasSize(2);
    }

    @Test
    void testBookingPreservesHistoryAfterDeletion() {
        User owner = userRepository.save(createUser("historytest"));
        Place place = placeRepository.save(createPlace(owner, "Parking History"));
        ChargingStation station = chargingStationRepository.save(createStation(place, "Borne History"));
        Vehicle vehicle = vehicleRepository.save(createVehicle(owner, "II-999"));

        Booking booking = bookingRepository.save(createBooking(vehicle, station, BookingState.COMPLETED));
        Long bookingId = booking.getId();

        // Suppression logique du véhicule et de la borne
        vehicle.setDeletedAt(LocalDateTime.now());
        station.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
        chargingStationRepository.save(station);

        // Vérifier que toutes les données de la réservation sont préservées
        Optional<Booking> foundBooking = bookingRepository.findById(bookingId);
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get().getVehicle().getRegistrationNumber()).isEqualTo("II-999");
        assertThat(foundBooking.get().getStation().getName()).isEqualTo("Borne History");
        assertThat(foundBooking.get().getState()).isEqualTo(BookingState.COMPLETED);
        assertThat(foundBooking.get().getFinalPrice()).isEqualTo(new BigDecimal("5.00"));
    }
}

