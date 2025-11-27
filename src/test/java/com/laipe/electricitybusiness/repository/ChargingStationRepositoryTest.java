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

    private User createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@example.com");
        u.setFirstName("Owner");
        u.setLastName("Three");
        u.setBirthDate(LocalDate.of(1975, 3, 3));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(false);
        u.setVerificationCode("345678");
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
        s.setLatitude(new BigDecimal("48.8566"));
        s.setLongitude(new BigDecimal("2.3522"));
        s.setPricePerKwh(new BigDecimal("0.25"));
        s.setPowerKw(new BigDecimal("22.00"));
        s.setPlace(place);
        s.setCreatedAt(LocalDateTime.now());
        return s;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User owner = userRepository.save(createUser("owner3"));
        Place place = placeRepository.save(createPlace(owner, "Parking Nord"));
        ChargingStation saved = chargingStationRepository.save(createStation(place, "Borne 1"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getDeletedAt()).isNull();

        List<ChargingStation> all = chargingStationRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<ChargingStation> found = chargingStationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Borne 1");

        chargingStationRepository.delete(found.get());
        assertThat(chargingStationRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testFindAllNotDeleted() {
        User owner = userRepository.save(createUser("owner4"));
        Place place = placeRepository.save(createPlace(owner, "Parking Test"));

        ChargingStation station1 = chargingStationRepository.save(createStation(place, "Borne Active 1"));
        ChargingStation station2 = chargingStationRepository.save(createStation(place, "Borne Active 2"));
        ChargingStation station3 = chargingStationRepository.save(createStation(place, "Borne Supprimée"));

        // Suppression logique de station3
        station3.setDeletedAt(LocalDateTime.now());
        chargingStationRepository.save(station3);

        List<ChargingStation> notDeleted = chargingStationRepository.findAllNotDeleted();

        assertThat(notDeleted).hasSizeGreaterThanOrEqualTo(2);
        assertThat(notDeleted).allMatch(cs -> cs.getDeletedAt() == null);
        assertThat(notDeleted).extracting(ChargingStation::getName)
                .contains("Borne Active 1", "Borne Active 2")
                .doesNotContain("Borne Supprimée");
    }

    @Test
    void testFindAllDeleted() {
        User owner = userRepository.save(createUser("owner5"));
        Place place = placeRepository.save(createPlace(owner, "Parking Archives"));

        ChargingStation station1 = chargingStationRepository.save(createStation(place, "Borne Active"));
        ChargingStation station2 = chargingStationRepository.save(createStation(place, "Borne Supprimée 1"));
        ChargingStation station3 = chargingStationRepository.save(createStation(place, "Borne Supprimée 2"));

        // Suppression logique de station2 et station3
        station2.setDeletedAt(LocalDateTime.now().minusDays(1));
        station3.setDeletedAt(LocalDateTime.now());
        chargingStationRepository.save(station2);
        chargingStationRepository.save(station3);

        List<ChargingStation> deleted = chargingStationRepository.findAllDeleted();

        assertThat(deleted).hasSizeGreaterThanOrEqualTo(2);
        assertThat(deleted).allMatch(cs -> cs.getDeletedAt() != null);
        assertThat(deleted).extracting(ChargingStation::getName)
                .contains("Borne Supprimée 1", "Borne Supprimée 2");
    }

    @Test
    void testSoftDeletePreservesData() {
        User owner = userRepository.save(createUser("owner6"));
        Place place = placeRepository.save(createPlace(owner, "Parking Temporaire"));
        ChargingStation station = chargingStationRepository.save(createStation(place, "Borne à Supprimer"));

        Long stationId = station.getId();
        assertThat(station.getDeletedAt()).isNull();

        // Suppression logique
        station.setDeletedAt(LocalDateTime.now());
        chargingStationRepository.save(station);

        // La borne existe toujours en base
        Optional<ChargingStation> found = chargingStationRepository.findById(stationId);
        assertThat(found).isPresent();
        assertThat(found.get().getDeletedAt()).isNotNull();
        assertThat(found.get().getName()).isEqualTo("Borne à Supprimer");
        assertThat(found.get().getPricePerKwh()).isEqualTo(new BigDecimal("0.25"));
    }

    @Test
    void testCreatedAtIsSetAutomatically() {
        User owner = userRepository.save(createUser("owner7"));
        Place place = placeRepository.save(createPlace(owner, "Parking Nouveau"));

        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);
        ChargingStation station = createStation(place, "Nouvelle Borne");
        ChargingStation saved = chargingStationRepository.save(station);
        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isAfter(beforeCreate);
        assertThat(saved.getCreatedAt()).isBefore(afterCreate);
    }

    @Test
    void testMultipleSoftDeletesOnSamePlace() {
        User owner = userRepository.save(createUser("owner8"));
        Place place = placeRepository.save(createPlace(owner, "Parking Multi"));

        ChargingStation station1 = chargingStationRepository.save(createStation(place, "Borne A"));
        ChargingStation station2 = chargingStationRepository.save(createStation(place, "Borne B"));
        ChargingStation station3 = chargingStationRepository.save(createStation(place, "Borne C"));

        // Suppression logique de station1 et station3
        station1.setDeletedAt(LocalDateTime.now().minusHours(2));
        station3.setDeletedAt(LocalDateTime.now().minusHours(1));
        chargingStationRepository.save(station1);
        chargingStationRepository.save(station3);

        List<ChargingStation> allStations = chargingStationRepository.findAll();
        assertThat(allStations).hasSize(3);

        List<ChargingStation> notDeleted = chargingStationRepository.findAllNotDeleted();
        assertThat(notDeleted.stream()
                .filter(s -> s.getPlace().getId().equals(place.getId())))
                .hasSize(1);

        List<ChargingStation> deleted = chargingStationRepository.findAllDeleted();
        assertThat(deleted.stream()
                .filter(s -> s.getPlace().getId().equals(place.getId())))
                .hasSize(2);
    }
}

