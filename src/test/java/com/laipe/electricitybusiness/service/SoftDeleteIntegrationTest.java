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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    private UserRepository userRepository;

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

        // construct services using real JPA repositories and mocked dependencies
        // ChargingStationService is required by PlaceService, construct it first
        this.chargingStationService = new ChargingStationService(chargingStationRepository, bookingRepo, placeRepository, geo, dateUtil);
        this.placeService = new PlaceService(placeRepository, chargingStationRepository, chargingStationService, userRepository);
        this.vehicleService = new VehicleService(vehicleRepository, mock(VehicleModelRepository.class), bookingRepo, userRepository);
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

    private Place buildPlace(User owner) {
        Place p = new Place();
        p.setName("Test Place " + System.nanoTime());
        p.setOwner(owner);
        p.setCreatedAt(LocalDateTime.now());
        return p;
    }

    private Vehicle buildVehicle(User owner) {
        Vehicle v = new Vehicle();
        v.setRegistrationNumber("RN" + System.nanoTime() % 10000);
        v.setOwner(owner);
        v.setModelId("model-1");
        v.setCreatedAt(LocalDateTime.now());
        return v;
    }

    private ChargingStation buildChargingStation(Place place) {
        ChargingStation cs = new ChargingStation();
        cs.setName("Station " + System.nanoTime());
        cs.setLatitude(BigDecimal.valueOf(45.0));
        cs.setLongitude(BigDecimal.valueOf(3.0));
        cs.setPricePerKwh(BigDecimal.valueOf(0.20));
        cs.setPowerKw(BigDecimal.valueOf(22));
        cs.setPlace(place);
        cs.setCreatedAt(LocalDateTime.now());
        return cs;
    }

    // ==================== EXISTING FILTERING TESTS ====================

    @Nested
    @DisplayName("Tests de filtrage après soft-delete")
    class FilteringTests {

        @Test
        void softDeleteAndFilteringForPlace() {
            // create owner
            User owner = buildUser();
            owner = userRepository.save(owner);
            Place p = buildPlace(owner);
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

            Vehicle v = buildVehicle(owner);
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
            Place p = buildPlace(owner);
            Place savedPlace = placeRepository.save(p);

            ChargingStation cs = buildChargingStation(savedPlace);
            ChargingStation saved = chargingStationRepository.save(cs);

            List<ChargingStation> all = chargingStationService.getAll();
            assertThat(all).extracting(ChargingStation::getId).contains(saved.getId());

            Optional<ChargingStation> deleted = chargingStationService.deleteById(saved.getId());
            assertThat(deleted).isPresent();
            assertThat(deleted.get().getDeletedAt()).isNotNull();

            List<ChargingStation> filtered = chargingStationService.getAll();
            assertThat(filtered).extracting(ChargingStation::getId).doesNotContain(saved.getId());

            Optional<ChargingStation> fromRepo = chargingStationRepository.findById(saved.getId());
            assertThat(fromRepo).isPresent();
            assertThat(fromRepo.get().getDeletedAt()).isNotNull();
        }
    }

    // ==================== TESTS: CANNOT DELETE ALREADY SOFT-DELETED ENTITY ====================

    @Nested
    @DisplayName("Tests: une entité soft-deleted ne peut plus être supprimée")
    class CannotDeleteAgainTests {

        @Test
        void deletingAlreadyDeletedPlace_throwsException() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));

            // First deletion should succeed
            Optional<Place> deleted = placeService.deleteById(place.getId());
            assertThat(deleted).isPresent();
            assertThat(deleted.get().getDeletedAt()).isNotNull();

            // Second deletion should throw exception
            assertThatThrownBy(() -> placeService.deleteById(place.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already deleted");
        }

        @Test
        void deletingAlreadyDeletedVehicle_throwsException() {
            User owner = userRepository.save(buildUser());
            Vehicle vehicle = vehicleRepository.save(buildVehicle(owner));

            // First deletion should succeed
            Optional<Vehicle> deleted = vehicleService.deleteById(vehicle.getId());
            assertThat(deleted).isPresent();
            assertThat(deleted.get().getDeletedAt()).isNotNull();

            // Second deletion should throw exception
            assertThatThrownBy(() -> vehicleService.deleteById(vehicle.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already deleted");
        }

        @Test
        void deletingAlreadyDeletedChargingStation_throwsException() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));
            ChargingStation station = chargingStationRepository.save(buildChargingStation(place));

            // First deletion should succeed
            Optional<ChargingStation> deleted = chargingStationService.deleteById(station.getId());
            assertThat(deleted).isPresent();
            assertThat(deleted.get().getDeletedAt()).isNotNull();

            // Second deletion should throw exception
            assertThatThrownBy(() -> chargingStationService.deleteById(station.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already deleted");
        }
    }

    // ==================== TESTS: CANNOT UPDATE SOFT-DELETED ENTITY ====================

    @Nested
    @DisplayName("Tests: une entité soft-deleted ne peut plus être modifiée")
    class CannotUpdateDeletedTests {

        @Test
        void updatingDeletedPlace_throwsException() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));

            // Soft-delete the place
            placeService.deleteById(place.getId());

            // Try to update
            Place updatedPlace = new Place();
            updatedPlace.setName("Updated Name");
            updatedPlace.setOwner(owner);

            assertThatThrownBy(() -> placeService.update(updatedPlace, place.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted");
        }

        @Test
        void updatingDeletedVehicle_throwsException() {
            User owner = userRepository.save(buildUser());
            Vehicle vehicle = vehicleRepository.save(buildVehicle(owner));

            // Soft-delete the vehicle
            vehicleService.deleteById(vehicle.getId());

            // Try to update
            Vehicle updatedVehicle = new Vehicle();
            updatedVehicle.setRegistrationNumber("NEW123");
            updatedVehicle.setOwner(owner);
            updatedVehicle.setModelId("model-2");

            assertThatThrownBy(() -> vehicleService.update(updatedVehicle, vehicle.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted");
        }

        @Test
        void updatingDeletedChargingStation_throwsException() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));
            ChargingStation station = chargingStationRepository.save(buildChargingStation(place));

            // Soft-delete the station
            chargingStationService.deleteById(station.getId());

            // Try to update
            ChargingStation updatedStation = new ChargingStation();
            updatedStation.setName("Updated Station");
            updatedStation.setLatitude(BigDecimal.valueOf(46.0));
            updatedStation.setLongitude(BigDecimal.valueOf(4.0));
            updatedStation.setPricePerKwh(BigDecimal.valueOf(0.25));
            updatedStation.setPowerKw(BigDecimal.valueOf(50));
            updatedStation.setPlace(place);

            assertThatThrownBy(() -> chargingStationService.update(updatedStation, station.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted");
        }
    }

    // ==================== TESTS: CAN READ DELETED ENTITIES WITH DEDICATED METHODS ====================

    @Nested
    @DisplayName("Tests: une entité soft-deleted peut être lue avec une méthode dédiée")
    class CanReadDeletedTests {

        @Test
        void deletedPlace_canBeReadWithFindAllDeleted() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));

            // Before deletion: not in findAllDeleted
            List<Place> deletedBefore = placeRepository.findAllDeleted();
            assertThat(deletedBefore).extracting(Place::getId).doesNotContain(place.getId());

            // Soft-delete
            placeService.deleteById(place.getId());

            // After deletion: present in findAllDeleted
            List<Place> deletedAfter = placeRepository.findAllDeleted();
            assertThat(deletedAfter).extracting(Place::getId).contains(place.getId());

            // Also accessible via findAllDeletedByOwnerId
            List<Place> deletedByOwner = placeRepository.findAllDeletedByOwnerId(owner.getId());
            assertThat(deletedByOwner).extracting(Place::getId).contains(place.getId());
        }

        @Test
        void deletedVehicle_canBeReadWithFindAllDeleted() {
            User owner = userRepository.save(buildUser());
            Vehicle vehicle = vehicleRepository.save(buildVehicle(owner));

            // Before deletion: not in findAllDeleted
            List<Vehicle> deletedBefore = vehicleRepository.findAllDeleted();
            assertThat(deletedBefore).extracting(Vehicle::getId).doesNotContain(vehicle.getId());

            // Soft-delete
            vehicleService.deleteById(vehicle.getId());

            // After deletion: present in findAllDeleted
            List<Vehicle> deletedAfter = vehicleRepository.findAllDeleted();
            assertThat(deletedAfter).extracting(Vehicle::getId).contains(vehicle.getId());

            // Also accessible via findAllDeletedByOwnerId
            List<Vehicle> deletedByOwner = vehicleRepository.findAllDeletedByOwnerId(owner.getId());
            assertThat(deletedByOwner).extracting(Vehicle::getId).contains(vehicle.getId());
        }

        @Test
        void deletedChargingStation_canBeReadWithFindAllDeleted() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));
            ChargingStation station = chargingStationRepository.save(buildChargingStation(place));

            // Before deletion: not in findAllDeleted
            List<ChargingStation> deletedBefore = chargingStationRepository.findAllDeleted();
            assertThat(deletedBefore).extracting(ChargingStation::getId).doesNotContain(station.getId());

            // Soft-delete
            chargingStationService.deleteById(station.getId());

            // After deletion: present in findAllDeleted
            List<ChargingStation> deletedAfter = chargingStationRepository.findAllDeleted();
            assertThat(deletedAfter).extracting(ChargingStation::getId).contains(station.getId());
        }

        @Test
        void deletedEntity_stillAccessibleViaFindById() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));

            // Soft-delete
            placeService.deleteById(place.getId());

            // Still accessible via findById (raw repository access)
            Optional<Place> fromRepo = placeRepository.findById(place.getId());
            assertThat(fromRepo).isPresent();
            assertThat(fromRepo.get().getDeletedAt()).isNotNull();
            assertThat(fromRepo.get().getName()).isEqualTo(place.getName());
        }
    }

    // ==================== TESTS: CANNOT CREATE RELATION WITH DELETED ENTITY ====================

    @Nested
    @DisplayName("Tests: une entité soft-deleted ne peut pas faire partie d'une relation (création)")
    class CannotCreateWithDeletedRelationTests {

        @Test
        void createPlaceForDeletedUser_throwsException() {
            User owner = userRepository.save(buildUser());

            // Soft-delete the user
            owner.setDeletedAt(LocalDateTime.now());
            userRepository.save(owner);

            // Try to create a place for the deleted user
            Place newPlace = new Place();
            newPlace.setName("New Place");
            newPlace.setOwner(owner);

            assertThatThrownBy(() -> placeService.create(newPlace))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted user");
        }

        @Test
        void createVehicleForDeletedUser_throwsException() {
            User owner = userRepository.save(buildUser());

            // Soft-delete the user
            owner.setDeletedAt(LocalDateTime.now());
            userRepository.save(owner);

            // Try to create a vehicle for the deleted user
            Vehicle newVehicle = new Vehicle();
            newVehicle.setRegistrationNumber("NEW123");
            newVehicle.setOwner(owner);
            newVehicle.setModelId("model-1");

            assertThatThrownBy(() -> vehicleService.create(newVehicle))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted user");
        }

        @Test
        void createChargingStationForDeletedPlace_throwsException() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));

            // Soft-delete the place directly via repository (no cascade)
            place.setDeletedAt(LocalDateTime.now());
            placeRepository.save(place);

            // Try to create a charging station for the deleted place
            ChargingStation newStation = new ChargingStation();
            newStation.setName("New Station");
            newStation.setLatitude(BigDecimal.valueOf(45.0));
            newStation.setLongitude(BigDecimal.valueOf(3.0));
            newStation.setPricePerKwh(BigDecimal.valueOf(0.20));
            newStation.setPowerKw(BigDecimal.valueOf(22));
            newStation.setPlace(place);

            assertThatThrownBy(() -> chargingStationService.create(newStation))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted place");
        }
    }

    // ==================== TESTS: CANNOT UPDATE RELATION WITH DELETED ENTITY ====================

    @Nested
    @DisplayName("Tests: une entité soft-deleted ne peut pas faire partie d'une relation (modification)")
    class CannotUpdateWithDeletedRelationTests {

        @Test
        void updatePlaceForDeletedOwner_throwsException() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));

            // Soft-delete the owner
            owner.setDeletedAt(LocalDateTime.now());
            userRepository.save(owner);

            // Try to update the place (owner is now deleted)
            Place updatedPlace = new Place();
            updatedPlace.setName("Updated Name");
            updatedPlace.setOwner(owner);

            assertThatThrownBy(() -> placeService.update(updatedPlace, place.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted user");
        }

        @Test
        void updateVehicleForDeletedOwner_throwsException() {
            User owner = userRepository.save(buildUser());
            Vehicle vehicle = vehicleRepository.save(buildVehicle(owner));

            // Soft-delete the owner
            owner.setDeletedAt(LocalDateTime.now());
            userRepository.save(owner);

            // Try to update the vehicle (owner is now deleted)
            Vehicle updatedVehicle = new Vehicle();
            updatedVehicle.setRegistrationNumber("UPDATED1");
            updatedVehicle.setOwner(owner);
            updatedVehicle.setModelId("model-2");

            assertThatThrownBy(() -> vehicleService.update(updatedVehicle, vehicle.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted user");
        }

        @Test
        void updateChargingStationForDeletedPlace_throwsException() {
            User owner = userRepository.save(buildUser());
            Place place = placeRepository.save(buildPlace(owner));
            ChargingStation station = chargingStationRepository.save(buildChargingStation(place));

            // Soft-delete the place directly via repository (bypassing service cascade)
            // to keep the station NOT deleted but its parent place deleted
            place.setDeletedAt(LocalDateTime.now());
            placeRepository.save(place);

            // Try to update the station (place is now deleted)
            ChargingStation updatedStation = new ChargingStation();
            updatedStation.setName("Updated Station");
            updatedStation.setLatitude(BigDecimal.valueOf(46.0));
            updatedStation.setLongitude(BigDecimal.valueOf(4.0));
            updatedStation.setPricePerKwh(BigDecimal.valueOf(0.30));
            updatedStation.setPowerKw(BigDecimal.valueOf(50));
            updatedStation.setPlace(place);

            assertThatThrownBy(() -> chargingStationService.update(updatedStation, station.getId()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("deleted place");
        }
    }
}

