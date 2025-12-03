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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ServiceDeletionRulesIntegrationTest {

    private static final AtomicInteger REG_COUNTER = new AtomicInteger();

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    private ChargingStationService chargingStationService;
    private PlaceService placeService;
    private VehicleService vehicleService;

    @BeforeEach
    void setup() {
        // mocks pour dépendances non-JPA
        var geo = mock(GeolocatorUtil.class);
        var dateUtil = mock(DateUtil.class);
        var mapper = mock(GetChargingStationMapper.class);

        // Construire les services en réutilisant les repositories réels
        this.chargingStationService = new ChargingStationService(chargingStationRepository, bookingRepository, geo, dateUtil, mapper);
        this.placeService = new PlaceService(placeRepository, chargingStationRepository, chargingStationService);
        this.vehicleService = new VehicleService(vehicleRepository, mock(VehicleModelRepository.class), bookingRepository);
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

    private ChargingStation buildStation(Place place) {
        ChargingStation cs = new ChargingStation();
        cs.setName("Station-");
        cs.setLatitude(BigDecimal.valueOf(45.0));
        cs.setLongitude(BigDecimal.valueOf(3.0));
        cs.setPricePerKwh(BigDecimal.valueOf(0.20));
        cs.setPowerKw(BigDecimal.valueOf(22));
        cs.setPlace(place);
        cs.setCreatedAt(LocalDateTime.now());
        return cs;
    }

    private Vehicle buildVehicle(User owner) {
        Vehicle v = new Vehicle();
        // Deterministic short registration number (2-15 chars)
        String reg = String.format("RN%04d", REG_COUNTER.incrementAndGet());
        v.setRegistrationNumber(reg);
        v.setOwner(owner);
        v.setModelId("model-1");
        v.setCreatedAt(LocalDateTime.now());
        return v;
    }

    private Booking buildBooking(Vehicle vehicle, ChargingStation station, BookingState state, LocalDateTime start, LocalDateTime end) {
        Booking b = new Booking();
        b.setVehicle(vehicle);
        b.setStation(station);
        b.setStartDate(start);
        b.setExpectedEndDate(end);
        b.setState(state);
        return b;
    }

    @Test
    void chargingStationDelete_refused_when_activeOrFutureBookingExists() {
        // owner, place, station
        User owner = userRepository.save(buildUser());
        Place place = new Place();
        place.setName("P1");
        place.setOwner(owner);
        place.setCreatedAt(LocalDateTime.now());
        place = placeRepository.save(place);

        ChargingStation station = chargingStationRepository.save(buildStation(place));

        // vehicle and booking accepted (active)
        Vehicle vehicle = vehicleRepository.save(buildVehicle(owner));
        bookingRepository.save(buildBooking(vehicle, station, BookingState.ACCEPTED, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));

        // Attempt to delete station should fail with InvalidBookingState
        assertThatThrownBy(() -> chargingStationService.deleteById(station.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot delete station");

        // Also ensure entity still exists and not soft-deleted
        var fromRepo = chargingStationRepository.findById(station.getId());
        assertThat(fromRepo).isPresent();
        assertThat(fromRepo.get().getDeletedAt()).isNull();
    }

    @Test
    void placeDelete_refused_when_associatedStationDeletionRefused() {
        User owner = userRepository.save(buildUser());
        Place place = new Place();
        place.setName("P2");
        place.setOwner(owner);
        place.setCreatedAt(LocalDateTime.now());
        place = placeRepository.save(place);

        ChargingStation station = chargingStationRepository.save(buildStation(place));

        Vehicle vehicle = vehicleRepository.save(buildVehicle(owner));
        // create a future booking (start in future) which should also block deletion
        bookingRepository.save(buildBooking(vehicle, station, BookingState.PENDING_ACCEPT, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2)));

        // Because ChargingStationService.checks for startDate.isAfter(now) OR active bookings,
        // deleting place will attempt to delete station and propagate the error -> expect exception
        Place finalPlace = place;
        assertThatThrownBy(() -> placeService.deleteById(finalPlace.getId()))
                .isInstanceOf(RuntimeException.class);

        // Ensure place is not soft-deleted
        var p = placeRepository.findById(place.getId());
        assertThat(p).isPresent();
        assertThat(p.get().getDeletedAt()).isNull();
    }

    @Test
    void vehicleDelete_cancels_pendingAnd_acceptedBookings_and_refuses_when_ongoing_exists() {
        User owner = userRepository.save(buildUser());
        Vehicle vehicle = vehicleRepository.save(buildVehicle(owner));

        // create station and place for bookings
        Place place = new Place();
        place.setName("P3");
        place.setOwner(owner);
        place.setCreatedAt(LocalDateTime.now());
        place = placeRepository.save(place);
        ChargingStation station = chargingStationRepository.save(buildStation(place));

        // 1) Test cancellation of PENDING_ACCEPT and ACCEPTED
        bookingRepository.save(buildBooking(vehicle, station, BookingState.PENDING_ACCEPT, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1)));
        bookingRepository.save(buildBooking(vehicle, station, BookingState.ACCEPTED, LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1)));

        var result = vehicleService.deleteById(vehicle.getId());
        // deletion should proceed (no ONGOING), vehicle soft-deleted
        assertThat(result).isPresent();
        assertThat(result.get().getDeletedAt()).isNotNull();

        // bookings should have been updated to CANCELLED
        List<Booking> bookingsAfter = bookingRepository.findAllByVehiculeId(vehicle.getId());
        assertThat(bookingsAfter).extracting(Booking::getState).contains(BookingState.CANCELLED);

        // 2) Now create a new vehicle with an ONGOING booking -> deletion refused
        Vehicle vehicle2 = vehicleRepository.save(buildVehicle(owner));
        bookingRepository.save(buildBooking(vehicle2, station, BookingState.ONGOING, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)));

        assertThatThrownBy(() -> vehicleService.deleteById(vehicle2.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot delete vehicle");

        // Vehicle2 should not be soft-deleted
        var v2 = vehicleRepository.findById(vehicle2.getId());
        assertThat(v2).isPresent();
        assertThat(v2.get().getDeletedAt()).isNull();

        // booking state should remain ONGOING
        List<Booking> bOngoing = bookingRepository.findAllByVehiculeId(vehicle2.getId());
        assertThat(bOngoing).isNotEmpty();
        assertThat(bOngoing).extracting(Booking::getState).contains(BookingState.ONGOING);
    }
}
