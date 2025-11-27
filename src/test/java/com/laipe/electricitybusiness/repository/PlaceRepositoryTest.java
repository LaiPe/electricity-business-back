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

    private User createUser(String username) {
        User u = new User();
        u.setUsername(username);
        u.setPassword("pwd");
        u.setEmail(username + "@example.com");
        u.setFirstName("Owner");
        u.setLastName("Two");
        u.setBirthDate(LocalDate.of(1980, 2, 2));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        u.setVerified(false);
        u.setVerificationCode("234567");
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

    @Test
    void testSaveFindAllFindByIdDelete() {
        User owner = userRepository.save(createUser("owner2"));
        Place saved = placeRepository.save(createPlace(owner, "Parking Central"));
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getDeletedAt()).isNull();

        List<Place> all = placeRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<Place> found = placeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Parking Central");

        placeRepository.delete(found.get());
        assertThat(placeRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testFindAllNotDeletedByOwnerId() {
        User owner1 = userRepository.save(createUser("placeowner1"));
        User owner2 = userRepository.save(createUser("placeowner2"));

        Place place1 = placeRepository.save(createPlace(owner1, "Place Active 1"));
        Place place2 = placeRepository.save(createPlace(owner1, "Place Active 2"));
        Place place3 = placeRepository.save(createPlace(owner1, "Place Supprimée"));
        Place place4 = placeRepository.save(createPlace(owner2, "Place Autre"));

        // Suppression logique de place3
        place3.setDeletedAt(LocalDateTime.now());
        placeRepository.save(place3);

        List<Place> notDeleted = placeRepository.findAllNotDeletedByOwnerId(owner1.getId());

        assertThat(notDeleted).hasSize(2);
        assertThat(notDeleted).extracting(Place::getName)
                .containsExactlyInAnyOrder("Place Active 1", "Place Active 2");
        assertThat(notDeleted).allMatch(p -> p.getOwner().getId().equals(owner1.getId()));
        assertThat(notDeleted).allMatch(p -> p.getDeletedAt() == null);
    }

    @Test
    void testFindAllDeletedByOwnerId() {
        User owner = userRepository.save(createUser("placeowner3"));

        Place place1 = placeRepository.save(createPlace(owner, "Place Active"));
        Place place2 = placeRepository.save(createPlace(owner, "Place Supprimée 1"));
        Place place3 = placeRepository.save(createPlace(owner, "Place Supprimée 2"));

        // Suppression logique de place2 et place3
        place2.setDeletedAt(LocalDateTime.now().minusDays(2));
        place3.setDeletedAt(LocalDateTime.now().minusDays(1));
        placeRepository.save(place2);
        placeRepository.save(place3);

        List<Place> deleted = placeRepository.findAllDeletedByOwnerId(owner.getId());

        assertThat(deleted).hasSize(2);
        assertThat(deleted).extracting(Place::getName)
                .containsExactlyInAnyOrder("Place Supprimée 1", "Place Supprimée 2");
        assertThat(deleted).allMatch(p -> p.getDeletedAt() != null);
    }

    @Test
    void testFindAllNotDeleted() {
        User owner = userRepository.save(createUser("placeowner4"));

        placeRepository.save(createPlace(owner, "Place Active A"));
        placeRepository.save(createPlace(owner, "Place Active B"));

        Place deletedPlace = placeRepository.save(createPlace(owner, "Place Supprimée X"));
        deletedPlace.setDeletedAt(LocalDateTime.now());
        placeRepository.save(deletedPlace);

        List<Place> notDeleted = placeRepository.findAllNotDeleted();

        assertThat(notDeleted).hasSizeGreaterThanOrEqualTo(2);
        assertThat(notDeleted).allMatch(p -> p.getDeletedAt() == null);
        assertThat(notDeleted).extracting(Place::getName)
                .contains("Place Active A", "Place Active B")
                .doesNotContain("Place Supprimée X");
    }

    @Test
    void testFindAllDeleted() {
        User owner = userRepository.save(createUser("placeowner5"));

        placeRepository.save(createPlace(owner, "Place Active C"));

        Place deletedPlace1 = placeRepository.save(createPlace(owner, "Place Supprimée Y"));
        Place deletedPlace2 = placeRepository.save(createPlace(owner, "Place Supprimée Z"));
        deletedPlace1.setDeletedAt(LocalDateTime.now().minusDays(1));
        deletedPlace2.setDeletedAt(LocalDateTime.now());
        placeRepository.save(deletedPlace1);
        placeRepository.save(deletedPlace2);

        List<Place> deleted = placeRepository.findAllDeleted();

        assertThat(deleted).hasSizeGreaterThanOrEqualTo(2);
        assertThat(deleted).allMatch(p -> p.getDeletedAt() != null);
        assertThat(deleted).extracting(Place::getName)
                .contains("Place Supprimée Y", "Place Supprimée Z");
    }

    @Test
    void testSoftDeletePreservesData() {
        User owner = userRepository.save(createUser("placeowner6"));
        Place place = placeRepository.save(createPlace(owner, "Place à Supprimer"));

        Long placeId = place.getId();
        assertThat(place.getDeletedAt()).isNull();

        // Suppression logique
        place.setDeletedAt(LocalDateTime.now());
        placeRepository.save(place);

        // Le lieu existe toujours en base
        Optional<Place> found = placeRepository.findById(placeId);
        assertThat(found).isPresent();
        assertThat(found.get().getDeletedAt()).isNotNull();
        assertThat(found.get().getName()).isEqualTo("Place à Supprimer");
    }

    @Test
    void testCreatedAtIsSetAutomatically() {
        User owner = userRepository.save(createUser("placeowner7"));
        LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

        Place place = createPlace(owner, "Nouveau Parking");
        Place saved = placeRepository.save(place);

        LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isAfter(beforeCreate);
        assertThat(saved.getCreatedAt()).isBefore(afterCreate);
    }
}

