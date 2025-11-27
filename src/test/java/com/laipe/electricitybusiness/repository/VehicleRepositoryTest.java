package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.UserRole;
import com.laipe.electricitybusiness.model.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@example.com");
        u.setFirstName("Owner");
        u.setLastName("One");
        u.setBirthDate(LocalDate.of(1985, 5, 5));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(false);
        u.setVerificationCode("123456");
        u.setCodeExpirationDate(LocalDateTime.now().plusHours(24));
        return u;
    }

    private Vehicle createVehicle(User owner, String regNumber) {
        Vehicle v = new Vehicle();
        v.setRegistrationNumber(regNumber);
        v.setOwner(owner);
        v.setModelId("model-1");
        v.setCreatedAt(LocalDateTime.now());
        return v;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User owner = userRepository.save(createUser("owner"));
        Vehicle saved = vehicleRepository.save(createVehicle(owner, "ABC-123"));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getDeletedAt()).isNull();

        List<Vehicle> all = vehicleRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<Vehicle> found = vehicleRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getRegistrationNumber()).isEqualTo("ABC-123");

        vehicleRepository.delete(found.get());
        assertThat(vehicleRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testFindAllNotDeletedByOwnerId() {
        User owner1 = userRepository.save(createUser("vehicleowner1"));
        User owner2 = userRepository.save(createUser("vehicleowner2"));

        Vehicle vehicle1 = vehicleRepository.save(createVehicle(owner1, "VEH-001"));
        Vehicle vehicle2 = vehicleRepository.save(createVehicle(owner1, "VEH-002"));
        Vehicle vehicle3 = vehicleRepository.save(createVehicle(owner1, "VEH-003"));
        Vehicle vehicle4 = vehicleRepository.save(createVehicle(owner2, "VEH-004"));

        // Suppression logique de vehicle3
        vehicle3.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle3);

        List<Vehicle> notDeleted = vehicleRepository.findAllNotDeletedByOwnerId(owner1.getId());

        assertThat(notDeleted).hasSize(2);
        assertThat(notDeleted).extracting(Vehicle::getRegistrationNumber)
                .containsExactlyInAnyOrder("VEH-001", "VEH-002");
        assertThat(notDeleted).allMatch(v -> v.getOwner().getId().equals(owner1.getId()));
        assertThat(notDeleted).allMatch(v -> v.getDeletedAt() == null);
    }

    @Test
    void testFindAllDeletedByOwnerId() {
        User owner = userRepository.save(createUser("vehicleowner3"));

        Vehicle vehicle1 = vehicleRepository.save(createVehicle(owner, "VEH-010"));
        Vehicle vehicle2 = vehicleRepository.save(createVehicle(owner, "VEH-011"));
        Vehicle vehicle3 = vehicleRepository.save(createVehicle(owner, "VEH-012"));

        // Suppression logique de vehicle1 et vehicle2
        vehicle1.setDeletedAt(LocalDateTime.now().minusDays(2));
        vehicle2.setDeletedAt(LocalDateTime.now().minusDays(1));
        vehicleRepository.save(vehicle1);
        vehicleRepository.save(vehicle2);

        List<Vehicle> deleted = vehicleRepository.findAllDeletedByOwnerId(owner.getId());

        assertThat(deleted).hasSize(2);
        assertThat(deleted).extracting(Vehicle::getRegistrationNumber)
                .containsExactlyInAnyOrder("VEH-010", "VEH-011");
        assertThat(deleted).allMatch(v -> v.getDeletedAt() != null);
    }

    @Test
    void testFindAllNotDeleted() {
        User owner = userRepository.save(createUser("vehicleowner4"));

        vehicleRepository.save(createVehicle(owner, "VEH-020"));
        vehicleRepository.save(createVehicle(owner, "VEH-021"));

        Vehicle deletedVehicle = vehicleRepository.save(createVehicle(owner, "VEH-022"));
        deletedVehicle.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(deletedVehicle);

        List<Vehicle> notDeleted = vehicleRepository.findAllNotDeleted();

        assertThat(notDeleted).hasSizeGreaterThanOrEqualTo(2);
        assertThat(notDeleted).allMatch(v -> v.getDeletedAt() == null);
        assertThat(notDeleted).extracting(Vehicle::getRegistrationNumber)
                .contains("VEH-020", "VEH-021")
                .doesNotContain("VEH-022");
    }

    @Test
    void testFindAllDeleted() {
        User owner = userRepository.save(createUser("vehicleowner5"));

        vehicleRepository.save(createVehicle(owner, "VEH-030"));

        Vehicle deletedVehicle1 = vehicleRepository.save(createVehicle(owner, "VEH-031"));
        Vehicle deletedVehicle2 = vehicleRepository.save(createVehicle(owner, "VEH-032"));
        deletedVehicle1.setDeletedAt(LocalDateTime.now().minusDays(1));
        deletedVehicle2.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(deletedVehicle1);
        vehicleRepository.save(deletedVehicle2);

        List<Vehicle> deleted = vehicleRepository.findAllDeleted();

        assertThat(deleted).hasSizeGreaterThanOrEqualTo(2);
        assertThat(deleted).allMatch(v -> v.getDeletedAt() != null);
        assertThat(deleted).extracting(Vehicle::getRegistrationNumber)
                .contains("VEH-031", "VEH-032");
    }

    @Test
    void testSoftDeletePreservesData() {
        User owner = userRepository.save(createUser("vehicleowner6"));
        Vehicle vehicle = vehicleRepository.save(createVehicle(owner, "VEH-040"));

        Long vehicleId = vehicle.getId();
        assertThat(vehicle.getDeletedAt()).isNull();

        // Suppression logique
        vehicle.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);

        // Le véhicule existe toujours en base
        Optional<Vehicle> found = vehicleRepository.findById(vehicleId);
        assertThat(found).isPresent();
        assertThat(found.get().getDeletedAt()).isNotNull();
        assertThat(found.get().getRegistrationNumber()).isEqualTo("VEH-040");
    }
}

