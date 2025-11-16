package com.laipe.electricitybusiness.repository;

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
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User u = new User();
        u.setUsername("jdoe");
        u.setPassword("secret");
        u.setEmail("jdoe@example.com");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setBirthDate(LocalDate.of(1990,1,1));
        u.setRole(UserRole.USER);
        u.setSigninDate(LocalDateTime.now());
        u.setBanned(false);
        return u;
    }

    @Test
    void testSaveFindAllFindByIdDelete() {
        User saved = userRepository.save(createUser());
        assertThat(saved.getId()).isNotNull();

        List<User> all = userRepository.findAll();
        assertThat(all).isNotEmpty();

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("jdoe@example.com");

        userRepository.delete(found.get());
        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }
}

