package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.dto.chargingstations.GetChargingStationMapper;
import com.laipe.electricitybusiness.model.ChargingStation;
import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.UserRole;
import com.laipe.electricitybusiness.model.Vehicle;
import com.laipe.electricitybusiness.repository.ChargingStationRepository;
import com.laipe.electricitybusiness.repository.PlaceRepository;
import com.laipe.electricitybusiness.repository.VehicleModelRepository;
import com.laipe.electricitybusiness.repository.VehicleRepository;
import com.laipe.electricitybusiness.repository.BookingRepository;
import com.laipe.electricitybusiness.repository.UserRepository;
import com.laipe.electricitybusiness.utils.DateUtil;
import com.laipe.electricitybusiness.utils.GeolocatorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class SoftDeleteIntegrationTest {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private UserRepository userRepository; // ajout pour persister les owners

    // services to test (constructed manually to avoid loading non-JPA beans like Mongo)
    private PlaceService placeService;
    private VehicleService vehicleService;
    private ChargingStationService chargingStationService;

    @BeforeEach
    void setup() {
        // mocks for non-JPA dependencies
        var bookingRepo = mock(BookingRepository.class);
        var geo = mock(GeolocatorUtil.class);
        var dateUtil = mock(DateUtil.class);
        var mapper = mock(GetChargingStationMapper.class);

        // construct services using real JPA repositories and mocked dependencies
        // ChargingStationService is required by PlaceService, construct it first
        this.chargingStationService = new ChargingStationService(chargingStationRepository, bookingRepo, geo, dateUtil, mapper);
        this.placeService = new PlaceService(placeRepository, chargingStationRepository, chargingStationService);
        this.vehicleService = new VehicleService(vehicleRepository, mock(VehicleModelRepository.class), bookingRepo);
    }

    private User buildUser() {
        User u = new User();
        u.setUsername("user" + System.nanoTime());
        u.setPassword("pwd");
        u.setEmail(u.getUsername() + "@example.com");
        u.setFirstName("First");
        u.setLastName("Last");
        u.setBirthDate(LocalDate.of(1990, 1, 1));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(true);
        u.setVerificationCode("code");
        u.setCodeExpirationDate(LocalDateTime.now().plusDays(1));
        return u;
    }

    @Test
    void softDeleteAndFilteringForPlace() {
        // create owner
        User owner = buildUser();
        owner = userRepository.save(owner); // Persist owner before using it as relation
        // save owner via placeRepository's entity manager cascade is not set; use place's owner relationship save through repository
        Place p = new Place();
        p.setName("Test Place");
        p.setOwner(owner);
        p.setCreatedAt(LocalDateTime.now());
        Place saved = placeRepository.save(p);

        // Vérifier présent dans getAll()
        List<Place> all = placeService.getAll();
        assertThat(all).extracting(Place::getId).contains(saved.getId());

        // soft-delete via service
        Optional<Place> deleted = placeService.deleteById(saved.getId());
        assertThat(deleted).isPresent();
        assertThat(deleted.get().getDeletedAt()).isNotNull();

        // getAll doit filtrer (PlaceService.getAll uses repo.findAllNotDeleted)
        List<Place> allAfter = placeService.getAll();
        assertThat(allAfter).extracting(Place::getId).doesNotContain(saved.getId());

        // repository.findById doit toujours retourner l'entité (accessible)
        Optional<Place> fromRepo = placeRepository.findById(saved.getId());
        assertThat(fromRepo).isPresent();
        assertThat(fromRepo.get().getDeletedAt()).isNotNull();
    }

    @Test
    void softDeleteAndFilteringForVehicle() {
        User owner = buildUser();
        owner = userRepository.save(owner);

        Vehicle v = new Vehicle();
        v.setRegistrationNumber("RN12345"); // immatriculation courte et valide (2-15 chars)
        v.setOwner(owner);
        v.setModelId("model-1");
        v.setCreatedAt(LocalDateTime.now());
        Vehicle saved = vehicleRepository.save(v);

        List<Vehicle> all = vehicleService.getAll();
        assertThat(all).extracting(Vehicle::getId).contains(saved.getId());

        Optional<Vehicle> deleted = vehicleService.deleteById(saved.getId());
        assertThat(deleted).isPresent();
        assertThat(deleted.get().getDeletedAt()).isNotNull();

        List<Vehicle> allAfter = vehicleService.getAll();
        assertThat(allAfter).extracting(Vehicle::getId).doesNotContain(saved.getId());

        Optional<Vehicle> fromRepo = vehicleRepository.findById(saved.getId());
        assertThat(fromRepo).isPresent();
        assertThat(fromRepo.get().getDeletedAt()).isNotNull();
    }

    @Test
    void softDeleteAndFilteringForChargingStation() {
        User owner = buildUser();
        owner = userRepository.save(owner);
        Place p = new Place();
        p.setName("Place for Station");
        p.setOwner(owner);
        p.setCreatedAt(LocalDateTime.now());
        Place savedPlace = placeRepository.save(p);

        ChargingStation cs = new ChargingStation();
        cs.setName("Station 1");
        cs.setLatitude(BigDecimal.valueOf(45.0));
        cs.setLongitude(BigDecimal.valueOf(3.0));
        cs.setPricePerKwh(BigDecimal.valueOf(0.20));
        cs.setPowerKw(BigDecimal.valueOf(22));
        cs.setPlace(savedPlace);
        cs.setCreatedAt(LocalDateTime.now());
        ChargingStation saved = chargingStationRepository.save(cs);

        // Service getAll (GenericJPAService.getAll) may not filter deleted; the test asserts that services must filter
        List<ChargingStation> all = chargingStationService.getAll();
        assertThat(all).extracting(ChargingStation::getId).contains(saved.getId());

        Optional<ChargingStation> deleted = chargingStationService.deleteById(saved.getId());
        assertThat(deleted).isPresent();
        assertThat(deleted.get().getDeletedAt()).isNotNull();

        List<ChargingStation> filtered = chargingStationService.getAll();
        // If ChargingStationService properly enforces filtering, this assertion should hold. If not, test will fail and indicate missing behavior.
        assertThat(filtered).extracting(ChargingStation::getId).doesNotContain(saved.getId());

        Optional<ChargingStation> fromRepo = chargingStationRepository.findById(saved.getId());
        assertThat(fromRepo).isPresent();
        assertThat(fromRepo.get().getDeletedAt()).isNotNull();
    }
}
