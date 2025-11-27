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
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("secret");
        u.setEmail(username + "@example.com");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setBirthDate(LocalDate.of(1990, 1, 1));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(false);
        u.setVerificationCode("123456");
        u.setCodeExpirationDate(LocalDateTime.now().plusHours(24));
        return u;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User saved = userRepository.save(createUser("jdoe"));
        assertThat(saved.getId()).isNotNull();

        List<User> all = userRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("jdoe@example.com");

        userRepository.delete(found.get());
        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testFindByUsername() {
        User user = userRepository.save(createUser("testuser"));

        Optional<User> found = userRepository.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    void testFindStationOwnerBookingById() {
        User stationOwner = userRepository.save(createUser("stationowner"));
        User vehicleOwner = userRepository.save(createUser("vehicleowner"));

        Place place = new Place();
        place.setName("Parking Test");
        place.setDescription("Test");
        place.setOwner(stationOwner);
        place.setCreatedAt(LocalDateTime.now());
        place = placeRepository.save(place);

        ChargingStation station = new ChargingStation();
        station.setName("Borne Test");
        station.setLatitude(new BigDecimal("48.0"));
        station.setLongitude(new BigDecimal("2.0"));
        station.setPricePerKwh(new BigDecimal("0.30"));
        station.setPowerKw(new BigDecimal("11.00"));
        station.setPlace(place);
        station.setCreatedAt(LocalDateTime.now());
        station = chargingStationRepository.save(station);

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber("XX-999");
        vehicle.setOwner(vehicleOwner);
        vehicle.setModelId("model-1");
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle = vehicleRepository.save(vehicle);

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setExpectedEndDate(LocalDateTime.now().plusDays(1).plusHours(2));
        booking.setFinalPrice(new BigDecimal("5.00"));
        booking.setFinalConsumptionKwh(new BigDecimal("10.00"));
        booking.setState(BookingState.PENDING_ACCEPT);
        booking.setVehicle(vehicle);
        booking.setStation(station);
        booking = bookingRepository.save(booking);

        Optional<User> foundOwner = userRepository.findStationOwnerBookingById(booking.getId());

        assertThat(foundOwner).isPresent();
        assertThat(foundOwner.get().getId()).isEqualTo(stationOwner.getId());
        assertThat(foundOwner.get().getUsername()).isEqualTo("stationowner");
    }

    @Test
    void testFindVehicleOwnerBookingById() {
        User stationOwner = userRepository.save(createUser("stationowner2"));
        User vehicleOwner = userRepository.save(createUser("vehicleowner2"));

        Place place = new Place();
        place.setName("Parking Test 2");
        place.setDescription("Test 2");
        place.setOwner(stationOwner);
        place.setCreatedAt(LocalDateTime.now());
        place = placeRepository.save(place);

        ChargingStation station = new ChargingStation();
        station.setName("Borne Test 2");
        station.setLatitude(new BigDecimal("48.0"));
        station.setLongitude(new BigDecimal("2.0"));
        station.setPricePerKwh(new BigDecimal("0.30"));
        station.setPowerKw(new BigDecimal("11.00"));
        station.setPlace(place);
        station.setCreatedAt(LocalDateTime.now());
        station = chargingStationRepository.save(station);

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber("YY-888");
        vehicle.setOwner(vehicleOwner);
        vehicle.setModelId("model-2");
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle = vehicleRepository.save(vehicle);

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setExpectedEndDate(LocalDateTime.now().plusDays(1).plusHours(2));
        booking.setFinalPrice(new BigDecimal("5.00"));
        booking.setFinalConsumptionKwh(new BigDecimal("10.00"));
        booking.setState(BookingState.ACCEPTED);
        booking.setVehicle(vehicle);
        booking.setStation(station);
        booking = bookingRepository.save(booking);

        Optional<User> foundOwner = userRepository.findVehicleOwnerBookingById(booking.getId());

        assertThat(foundOwner).isPresent();
        assertThat(foundOwner.get().getId()).isEqualTo(vehicleOwner.getId());
        assertThat(foundOwner.get().getUsername()).isEqualTo("vehicleowner2");
    }

    @Test
    void testFindStationOwnerBookingByIdNotFound() {
        Optional<User> foundOwner = userRepository.findStationOwnerBookingById(99999L);
        assertThat(foundOwner).isEmpty();
    }

    @Test
    void testFindVehicleOwnerBookingByIdNotFound() {
        Optional<User> foundOwner = userRepository.findVehicleOwnerBookingById(99999L);
        assertThat(foundOwner).isEmpty();
    }

    @Test
    void testFindOwnersWithSoftDeletedResources() {
        User stationOwner = userRepository.save(createUser("softdeletetest1"));
        User vehicleOwner = userRepository.save(createUser("softdeletetest2"));

        Place place = new Place();
        place.setName("Parking Deleted");
        place.setDescription("Test deleted");
        place.setOwner(stationOwner);
        place.setCreatedAt(LocalDateTime.now());
        place = placeRepository.save(place);

        ChargingStation station = new ChargingStation();
        station.setName("Borne Deleted");
        station.setLatitude(new BigDecimal("48.0"));
        station.setLongitude(new BigDecimal("2.0"));
        station.setPricePerKwh(new BigDecimal("0.30"));
        station.setPowerKw(new BigDecimal("11.00"));
        station.setPlace(place);
        station.setCreatedAt(LocalDateTime.now());
        station = chargingStationRepository.save(station);

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber("ZZ-000");
        vehicle.setOwner(vehicleOwner);
        vehicle.setModelId("model-3");
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle = vehicleRepository.save(vehicle);

        Booking booking = new Booking();
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setExpectedEndDate(LocalDateTime.now().plusDays(1).plusHours(2));
        booking.setFinalPrice(new BigDecimal("5.00"));
        booking.setFinalConsumptionKwh(new BigDecimal("10.00"));
        booking.setState(BookingState.COMPLETED);
        booking.setVehicle(vehicle);
        booking.setStation(station);
        booking = bookingRepository.save(booking);

        // Suppression logique du véhicule et de la borne
        vehicle.setDeletedAt(LocalDateTime.now());
        station.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
        chargingStationRepository.save(station);

        // Les propriétaires doivent toujours être trouvés malgré la suppression logique
        Optional<User> foundStationOwner = userRepository.findStationOwnerBookingById(booking.getId());
        assertThat(foundStationOwner).isPresent();
        assertThat(foundStationOwner.get().getId()).isEqualTo(stationOwner.getId());

        Optional<User> foundVehicleOwner = userRepository.findVehicleOwnerBookingById(booking.getId());
        assertThat(foundVehicleOwner).isPresent();
        assertThat(foundVehicleOwner.get().getId()).isEqualTo(vehicleOwner.getId());
    }
}

