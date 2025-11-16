package com.laipe.electricitybusiness.repository;

import com.laipe.electricitybusiness.model.ChargingStation;
import com.laipe.electricitybusiness.model.Place;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.UserRole;
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
public class ChargingStationRepositoryTest {

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User u = new User();
        u.setUsername("owner3");
        u.setPassword("pwd");
        u.setEmail("owner3@example.com");
        u.setFirstName("Owner");
        u.setLastName("Three");
        u.setBirthDate(LocalDate.of(1975,3,3));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        return u;
    }

    private Place createPlace(User owner) {
        Place p = new Place();
        p.setName("Parking Nord");
        p.setOwner(owner);
        return p;
    }

    private ChargingStation createStation(Place place) {
        ChargingStation s = new ChargingStation();
        s.setName("Borne 1");
        s.setLatitude(new BigDecimal("48.8566"));
        s.setLongitude(new BigDecimal("2.3522"));
        s.setPricePerKwh(new BigDecimal("0.25"));
        s.setPowerKw(new BigDecimal("22.00"));
        s.setPlace(place);
        return s;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User owner = userRepository.save(createUser());
        Place place = placeRepository.save(createPlace(owner));
        ChargingStation saved = chargingStationRepository.save(createStation(place));
        assertThat(saved.getId()).isNotNull();

        List<ChargingStation> all = chargingStationRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<ChargingStation> found = chargingStationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Borne 1");

        chargingStationRepository.delete(found.get());
        assertThat(chargingStationRepository.findById(saved.getId())).isEmpty();
    }
}

