package com.laipe.electricitybusiness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laipe.electricitybusiness.dto.user.GetUserDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

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
    @DisplayName("Test CRUD complet sur l'entité User")
    void testUserCRUD() throws Exception {
        // ========================================
        // 1. CREATE - Créer un utilisateur valide
        // ========================================
        PostUserDTO createUserDTO = createValidUserDTO();
        String createUserJson = objectMapper.writeValueAsString(createUserDTO);

        String createResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createUserJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.last_name").value("Doe"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.banned").value(false))
                .andExpect(jsonPath("$.signin_date").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GetUserDTO createdUser = objectMapper.readValue(createResponse, GetUserDTO.class);
        Long userId = createdUser.getId();

        // Vérifier que l'utilisateur a été créé en base
        assertThat(userRepository.findById(userId)).isPresent();

        // ========================================
        // 2. READ - Récupérer l'utilisateur créé
        // ========================================
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        // ========================================
        // 3. READ ALL - Récupérer tous les utilisateurs
        // ========================================
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(userId));

        // ========================================
        // 4. UPDATE - Mettre à jour l'utilisateur
        // ========================================
        PostUserDTO updateUserDTO = createValidUserDTO();
        updateUserDTO.setUsername("updateduser");
        updateUserDTO.setEmail("updated@example.com");
        updateUserDTO.setFirstName("Jane");
        updateUserDTO.setLastName("Smith");
        updateUserDTO.setRole(UserRole.ADMIN);
        updateUserDTO.setIban("FR7630001007941234567890185");

        String updateUserJson = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.first_name").value("Jane"))
                .andExpect(jsonPath("$.last_name").value("Smith"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        // ========================================
        // 5. DELETE - Supprimer l'utilisateur
        // ========================================
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        // Vérifier que l'utilisateur a été supprimé de la base
        assertThat(userRepository.findById(userId)).isEmpty();

        // ========================================
        // 6. READ - Vérifier que l'utilisateur n'existe plus (404)
        // ========================================
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test validation des DTOs - Champs manquants")
    void testValidationMissingFields() throws Exception {
        PostUserDTO invalidUserDTO = new PostUserDTO();
        // Ne pas définir les champs obligatoires
        String invalidUserJson = objectMapper.writeValueAsString(invalidUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test validation des DTOs - Email invalide")
    void testValidationInvalidEmail() throws Exception {
        PostUserDTO invalidUserDTO = createValidUserDTO();
        invalidUserDTO.setEmail("invalid-email");
        String invalidUserJson = objectMapper.writeValueAsString(invalidUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test validation des DTOs - Date de naissance future")
    void testValidationFutureBirthDate() throws Exception {
        PostUserDTO invalidUserDTO = createValidUserDTO();
        invalidUserDTO.setBirthDate(LocalDate.now().plusDays(1));
        String invalidUserJson = objectMapper.writeValueAsString(invalidUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test validation des DTOs - Champs vides")
    void testValidationEmptyFields() throws Exception {
        PostUserDTO invalidUserDTO = createValidUserDTO();
        invalidUserDTO.setUsername("");
        invalidUserDTO.setPassword("");
        invalidUserDTO.setEmail("");
        invalidUserDTO.setFirstName("");
        invalidUserDTO.setLastName("");
        String invalidUserJson = objectMapper.writeValueAsString(invalidUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test erreur 404 - Utilisateur inexistant")
    void testNotFoundUser() throws Exception {
        // Test GET avec ID inexistant
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        // Test PUT avec ID inexistant
        PostUserDTO updateUserDTO = createValidUserDTO();
        String updateUserJson = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isNotFound());

        // Test DELETE avec ID inexistant
        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test création utilisateur ADMIN")
    void testCreateAdminUser() throws Exception {
        PostUserDTO adminUserDTO = createValidUserDTO();
        adminUserDTO.setUsername("adminuser");
        adminUserDTO.setEmail("admin@example.com");
        adminUserDTO.setRole(UserRole.ADMIN);
        adminUserDTO.setIban("FR7630001007941234567890185");

        String adminUserJson = objectMapper.writeValueAsString(adminUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("Test création utilisateur sans IBAN")
    void testCreateUserWithoutIban() throws Exception {
        PostUserDTO userWithoutIbanDTO = createValidUserDTO();
        userWithoutIbanDTO.setUsername("noibanuser");
        userWithoutIbanDTO.setEmail("noiban@example.com");
        userWithoutIbanDTO.setIban(null);

        String userWithoutIbanJson = objectMapper.writeValueAsString(userWithoutIbanDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userWithoutIbanJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").doesNotExist());
    }

    @Test
    @DisplayName("Test validation IBAN invalide")
    void testValidationInvalidIban() throws Exception {
        PostUserDTO invalidIbanUserDTO = createValidUserDTO();
        invalidIbanUserDTO.setUsername("invalidibanuser");
        invalidIbanUserDTO.setEmail("invalidiban@example.com");
        invalidIbanUserDTO.setIban("INVALID");

        String invalidIbanUserJson = objectMapper.writeValueAsString(invalidIbanUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidIbanUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test JSON malformé")
    void testMalformedJson() throws Exception {
        String malformedJson = "{ invalid json }";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test avec caractères spéciaux")
    void testSpecialCharacters() throws Exception {
        PostUserDTO specialCharUserDTO = createValidUserDTO();
        specialCharUserDTO.setUsername("special_chars_user");
        specialCharUserDTO.setEmail("special@example.com");
        specialCharUserDTO.setFirstName("Special<>\"'&");
        specialCharUserDTO.setLastName("Chars!@#$%");

        String specialCharUserJson = objectMapper.writeValueAsString(specialCharUserDTO);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(specialCharUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Special<>\"'&"))
                .andExpect(jsonPath("$.last_name").value("Chars!@#$%"));
    }

    private PostUserDTO createValidUserDTO() {
        PostUserDTO userDTO = new PostUserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password123");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        userDTO.setRole(UserRole.USER);
        return userDTO;
    }
} 