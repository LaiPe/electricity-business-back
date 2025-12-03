package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.dto.chargingstations.GetChargingStationMapper;
import com.laipe.electricitybusiness.model.*;
import com.laipe.electricitybusiness.repository.*;
import com.laipe.electricitybusiness.utils.DateUtil;
import com.laipe.electricitybusiness.utils.GeolocatorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ServiceUserDeletionIntegrationTest {

    private static final AtomicInteger REG_COUNTER = new AtomicInteger();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // VehicleModelRepository is a Mongo repository (not part of JPA slice). We'll mock it in setup().
    private VehicleModelRepository vehicleModelRepository;

    private ChargingStationService chargingStationService;
    private PlaceService placeService;
    private VehicleService vehicleService;
    private UserService userService;

    @BeforeEach
    void setup() {
        var geo = mock(GeolocatorUtil.class);
        var dateUtil = mock(DateUtil.class);
        var mapper = mock(GetChargingStationMapper.class);

        // mock the mongo-backed repo used by VehicleService
        this.vehicleModelRepository = mock(VehicleModelRepository.class);

        // construct services with real repos and mocked util dependencies
        this.chargingStationService = new ChargingStationService(chargingStationRepository, bookingRepository, geo, dateUtil, mapper);
        this.placeService = new PlaceService(placeRepository, chargingStationRepository, chargingStationService);
        this.vehicleService = new VehicleService(vehicleRepository, this.vehicleModelRepository, bookingRepository);

        // For UserService we need passwordEncoder and verificationCodeService; use simple mocks/real encoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        var verificationCodeService = mock(com.laipe.electricitybusiness.service.VerificationCodeService.class);

        this.userService = new UserService(userRepository, encoder, verificationCodeService, placeService, placeRepository, vehicleService, vehicleRepository);
    }

    private User buildUser() {
        User u = new User();
        u.setUsername("user" + System.nanoTime());
        u.setPassword("pwd");
        u.setEmail(u.getUsername() + "@example.com");
        u.setFirstName("First");
        u.setLastName("Last");
        u.setBirthDate(LocalDate.of(1990,1,1));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(true);
        u.setVerificationCode("code");
        u.setCodeExpirationDate(LocalDateTime.now().plusDays(1));
        return u;
    }

    private Vehicle buildVehicle(User owner) {
        Vehicle v = new Vehicle();
        String reg = String.format("RN%04d", REG_COUNTER.incrementAndGet());
        v.setRegistrationNumber(reg);
        v.setOwner(owner);
        v.setModelId("model-1");
        v.setCreatedAt(LocalDateTime.now());
        return v;
    }

    private ChargingStation buildStation(Place place) {
        ChargingStation cs = new ChargingStation();
        cs.setName("Station-test");
        cs.setLatitude(BigDecimal.valueOf(45.0));
        cs.setLongitude(BigDecimal.valueOf(3.0));
        cs.setPricePerKwh(BigDecimal.valueOf(0.20));
        cs.setPowerKw(BigDecimal.valueOf(22));
        cs.setPlace(place);
        cs.setCreatedAt(LocalDateTime.now());
        return cs;
    }

    private Booking buildBooking(Vehicle vehicle, ChargingStation station, BookingState state) {
        Booking b = new Booking();
        b.setVehicle(vehicle);
        b.setStation(station);
        b.setStartDate(LocalDateTime.now().minusHours(1));
        b.setExpectedEndDate(LocalDateTime.now().plusHours(1));
        b.setState(state);
        return b;
    }

    @Test
    void deleteUser_success_softDeletes_properties_and_anonymizes_user() {
        // create user with one place and one vehicle, no blocking bookings
        User user = userRepository.save(buildUser());

        Place place = new Place();
        place.setName("MyPlace");
        place.setOwner(user);
        place.setCreatedAt(LocalDateTime.now());
        place = placeRepository.save(place);

        Vehicle vehicle = vehicleRepository.save(buildVehicle(user));

        // No bookings created -> vehicleService.deleteById should succeed

        var result = userService.deleteById(user.getId());
        assertThat(result).isPresent();
        User deleted = result.get();

        // user is anonymized
        assertThat(deleted.getUsername()).startsWith("deleted_user_");
        assertThat(deleted.getEmail()).contains("@deleted.com");
        assertThat(deleted.getFirstName()).isEqualTo("Deleted");
        assertThat(deleted.getLastName()).isEqualTo("User");
        assertThat(deleted.getBirthDate()).isEqualTo(LocalDate.MIN);
        assertThat(deleted.getDeletedAt()).isNotNull();

        // properties should be soft-deleted (if deletion succeeded)
        var v = vehicleRepository.findById(vehicle.getId());
        assertThat(v).isPresent();
        assertThat(v.get().getDeletedAt()).isNotNull();

        var p = placeRepository.findById(place.getId());
        assertThat(p).isPresent();
        assertThat(p.get().getDeletedAt()).isNotNull();
    }

    @Test
    void deleteUser_refused_if_vehicleDeletion_refused_and_user_not_anonymized() {
        // create user with vehicle that has ONGOING booking -> vehicle deletion refused
        User user = userRepository.save(buildUser());

        Place place = new Place();
        place.setName("MyPlace2");
        place.setOwner(user);
        place.setCreatedAt(LocalDateTime.now());
        place = placeRepository.save(place);

        Vehicle vehicle = vehicleRepository.save(buildVehicle(user));

        ChargingStation station = chargingStationRepository.save(buildStation(place));

        // create ONGOING booking which should block vehicle deletion
        bookingRepository.save(buildBooking(vehicle, station, BookingState.ONGOING));

        // deletion should throw and user should remain unchanged
        assertThatThrownBy(() -> userService.deleteById(user.getId()))
                .isInstanceOf(RuntimeException.class);

        var existingUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(existingUser.getDeletedAt()).isNull();
        assertThat(existingUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(existingUser.getEmail()).isEqualTo(user.getEmail());
    }
}
