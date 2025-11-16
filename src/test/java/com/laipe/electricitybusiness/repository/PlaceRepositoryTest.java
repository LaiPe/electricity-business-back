package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PlaceRepositoryTest {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User u = new User();
        u.setUsername("owner2");
        u.setPassword("pwd");
        u.setEmail("owner2@example.com");
        u.setFirstName("Owner");
        u.setLastName("Two");
        u.setBirthDate(LocalDate.of(1980,2,2));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        return u;
    }

    private Place createPlace(User owner) {
        Place p = new Place();
        p.setName("Parking Central");
        p.setInstructions("Suivez les panneaux");
        p.setOwner(owner);
        return p;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User owner = userRepository.save(createUser());
        Place saved = placeRepository.save(createPlace(owner));
        assertThat(saved.getId()).isNotNull();

        List<Place> all = placeRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<Place> found = placeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Parking Central");

        placeRepository.delete(found.get());
        assertThat(placeRepository.findById(saved.getId())).isEmpty();
    }
}

