package com.laipe.electricitybusiness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laipe.electricitybusiness.dto.user.PostUserDTO;
import com.laipe.electricitybusiness.model.UserRole;
import com.laipe.electricitybusiness.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class UserControllerPerformanceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test performance - Création multiple d'utilisateurs")
    void testMultipleUserCreation() throws Exception {
        int numberOfUsers = 10;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfUsers; i++) {
            PostUserDTO userDTO = createUserDTO("user" + i, "user" + i + "@example.com");
            String userJson = objectMapper.writeValueAsString(userDTO);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isOk());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Vérifier que tous les utilisateurs ont été créés
        assertThat(userRepository.count()).isEqualTo(numberOfUsers);

        // Vérifier que le temps d'exécution est raisonnable (moins de 5 secondes pour 10 utilisateurs)
        assertThat(duration).isLessThan(5000);
    }

    @Test
    @DisplayName("Test performance - Création concurrente d'utilisateurs")
    void testConcurrentUserCreation() throws Exception {
        int numberOfUsers = 5;
        int numberOfThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        long startTime = System.currentTimeMillis();

        CompletableFuture<?>[] futures = new CompletableFuture[numberOfUsers];
        for (int i = 0; i < numberOfUsers; i++) {
            final int userIndex = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    PostUserDTO userDTO = createUserDTO("concurrent_user" + userIndex, 
                                                      "concurrent" + userIndex + "@example.com");
                    String userJson = objectMapper.writeValueAsString(userDTO);

                    mockMvc.perform(post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(userJson))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
        }

        // Attendre que toutes les tâches soient terminées
        CompletableFuture.allOf(futures).join();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Vérifier que tous les utilisateurs ont été créés
        assertThat(userRepository.count()).isEqualTo(numberOfUsers);

        // Vérifier que le temps d'exécution est raisonnable
        assertThat(duration).isLessThan(10000);
    }

    @Test
    @DisplayName("Test performance - Récupération multiple d'utilisateurs")
    void testMultipleUserRetrieval() throws Exception {
        // Créer d'abord quelques utilisateurs
        for (int i = 0; i < 5; i++) {
            PostUserDTO userDTO = createUserDTO("perf_user" + i, "perf" + i + "@example.com");
            String userJson = objectMapper.writeValueAsString(userDTO);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isOk());
        }

        // Test de récupération multiple
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Vérifier que le temps d'exécution est raisonnable
        assertThat(duration).isLessThan(3000);
    }

    @Test
    @DisplayName("Test performance - Validation rapide de multiples DTOs")
    void testFastDTOValidation() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            PostUserDTO userDTO = createUserDTO("valid_user" + i, "valid" + i + "@example.com");
            String userJson = objectMapper.writeValueAsString(userDTO);

            // Test de validation (sans sauvegarde)
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userJson))
                    .andExpect(status().isOk());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Vérifier que le temps d'exécution est raisonnable
        assertThat(duration).isLessThan(15000); // 15 secondes pour 100 utilisateurs
    }

    @Test
    @DisplayName("Test performance - Gestion des erreurs de validation")
    void testValidationErrorHandling() throws Exception {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 50; i++) {
            PostUserDTO invalidUserDTO = new PostUserDTO();
            // DTO invalide sans champs obligatoires
            String invalidUserJson = objectMapper.writeValueAsString(invalidUserDTO);

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidUserJson))
                    .andExpect(status().isBadRequest());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Vérifier que le temps d'exécution est raisonnable
        assertThat(duration).isLessThan(5000); // 5 secondes pour 50 erreurs
    }

    @Test
    @DisplayName("Test performance - Stress test avec mélange d'opérations")
    void testStressTest() throws Exception {
        long startTime = System.currentTimeMillis();

        // Mélange d'opérations : création, lecture, erreurs
        for (int i = 0; i < 20; i++) {
            if (i % 4 == 0) {
                // Création d'utilisateur valide
                PostUserDTO userDTO = createUserDTO("stress_user" + i, "stress" + i + "@example.com");
                String userJson = objectMapper.writeValueAsString(userDTO);

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson))
                        .andExpect(status().isOk());
            } else if (i % 4 == 1) {
                // Lecture de tous les utilisateurs
                mockMvc.perform(get("/users"))
                        .andExpect(status().isOk());
            } else if (i % 4 == 2) {
                // Test avec utilisateur inexistant
                mockMvc.perform(get("/users/999"))
                        .andExpect(status().isNotFound());
            } else {
                // Test avec DTO invalide
                PostUserDTO invalidUserDTO = new PostUserDTO();
                String invalidUserJson = objectMapper.writeValueAsString(invalidUserDTO);

                mockMvc.perform(post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidUserJson))
                        .andExpect(status().isBadRequest());
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Vérifier que le temps d'exécution est raisonnable
        assertThat(duration).isLessThan(10000); // 10 secondes pour 20 opérations mixtes
    }

    private PostUserDTO createUserDTO(String username, String email) {
        PostUserDTO userDTO = new PostUserDTO();
        userDTO.setUsername(username);
        userDTO.setPassword("password123");
        userDTO.setEmail(email);
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        userDTO.setRole(UserRole.USER);
        return userDTO;
    }
} 