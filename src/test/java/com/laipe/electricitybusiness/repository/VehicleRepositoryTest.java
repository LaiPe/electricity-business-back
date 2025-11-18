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

    private User createUser() {
        User u = new User();
        u.setUsername("owner");
        u.setPassword("pwd");
        u.setEmail("owner@example.com");
        u.setFirstName("Owner");
        u.setLastName("One");
        u.setBirthDate(LocalDate.of(1985,5,5));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(false);
        u.setVerificationCode("123456");
        u.setCodeExpirationDate(LocalDateTime.now().plusHours(24));
        return u;
    }

    private Vehicle createVehicle(User owner) {
        Vehicle v = new Vehicle();
        v.setRegistrationNumber("ABC-123");
        v.setOwner(owner);
        v.setModelId("model-1");
        return v;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User owner = userRepository.save(createUser());
        Vehicle saved = vehicleRepository.save(createVehicle(owner));
        assertThat(saved.getId()).isNotNull();

        List<Vehicle> all = vehicleRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<Vehicle> found = vehicleRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getRegistrationNumber()).isEqualTo("ABC-123");

        vehicleRepository.delete(found.get());
        assertThat(vehicleRepository.findById(saved.getId())).isEmpty();
    }
}

