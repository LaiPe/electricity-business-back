package com.laipe.electricitybusiness.util;

import com.laipe.electricitybusiness.dto.UserDTO;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MappingUtilTest {

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        // Initialize test User entity
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setPassword("securePassword123");
        testUser.setEmail("john.doe@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setBirthDate(LocalDate.of(1990, 5, 15));
        testUser.setRole(UserRole.USER);
        testUser.setIban("FR1420041010050500013M02606");
        testUser.setSigninDate(LocalDateTime.of(2023, 1, 15, 10, 30));
        testUser.setBanned(false);

        // Initialize test UserDTO
        testUserDTO = new UserDTO();
        testUserDTO.setId(2L);
        testUserDTO.setUsername("jane_smith");
        testUserDTO.setEmail("jane.smith@example.com");
        testUserDTO.setFirstName("Jane");
        testUserDTO.setLastName("Smith");
        testUserDTO.setBirthDate(LocalDate.of(1985, 8, 20));
        testUserDTO.setRole(UserRole.ADMIN);
        testUserDTO.setSigninDate(LocalDateTime.of(2023, 2, 10, 14, 45));
        testUserDTO.setBanned(true);
    }

    @Test
    @DisplayName("Should convert User entity to UserDTO successfully")
    void testToDTO_Success() {
        // When
        UserDTO resultDTO = ModelUtil.toDTO(testUser);

        // Then
        assertNotNull(resultDTO);
        assertEquals(testUser.getId(), resultDTO.getId());
        assertEquals(testUser.getUsername(), resultDTO.getUsername());
        assertEquals(testUser.getEmail(), resultDTO.getEmail());
        assertEquals(testUser.getFirstName(), resultDTO.getFirstName());
        assertEquals(testUser.getLastName(), resultDTO.getLastName());
        assertEquals(testUser.getBirthDate(), resultDTO.getBirthDate());
        assertEquals(testUser.getRole(), resultDTO.getRole());
        assertEquals(testUser.getSigninDate(), resultDTO.getSigninDate());
        assertEquals(testUser.getBanned(), resultDTO.getBanned());

        // Note: password and iban should not be present in DTO (fields don't exist in DTO)
    }

    @Test
    @DisplayName("Should convert UserDTO to User entity successfully")
    void testToEntity_Success() {
        // When
        User resultEntity = ModelUtil.toEntity(testUserDTO);

        // Then
        assertNotNull(resultEntity);
        assertEquals(testUserDTO.getId(), resultEntity.getId());
        assertEquals(testUserDTO.getUsername(), resultEntity.getUsername());
        assertEquals(testUserDTO.getEmail(), resultEntity.getEmail());
        assertEquals(testUserDTO.getFirstName(), resultEntity.getFirstName());
        assertEquals(testUserDTO.getLastName(), resultEntity.getLastName());
        assertEquals(testUserDTO.getBirthDate(), resultEntity.getBirthDate());
        assertEquals(testUserDTO.getRole(), resultEntity.getRole());
        assertEquals(testUserDTO.getSigninDate(), resultEntity.getSigninDate());
        assertEquals(testUserDTO.getBanned(), resultEntity.getBanned());

        // Fields not present in DTO should be null in entity
        assertNull(resultEntity.getPassword());
        assertNull(resultEntity.getIban());
    }

    @Test
    @DisplayName("Should perform bidirectional conversion correctly")
    void testBidirectionalConversion() {
        // When - Convert entity to DTO, then back to entity
        UserDTO intermediateDTO = ModelUtil.toDTO(testUser);
        User finalEntity = ModelUtil.toEntity(intermediateDTO);

        // Then - Compare common fields
        assertEquals(testUser.getId(), finalEntity.getId());
        assertEquals(testUser.getUsername(), finalEntity.getUsername());
        assertEquals(testUser.getEmail(), finalEntity.getEmail());
        assertEquals(testUser.getFirstName(), finalEntity.getFirstName());
        assertEquals(testUser.getLastName(), finalEntity.getLastName());
        assertEquals(testUser.getBirthDate(), finalEntity.getBirthDate());
        assertEquals(testUser.getRole(), finalEntity.getRole());
        assertEquals(testUser.getSigninDate(), finalEntity.getSigninDate());
        assertEquals(testUser.getBanned(), finalEntity.getBanned());

        // Fields that exist only in entity should be lost
        assertNull(finalEntity.getPassword());
        assertNull(finalEntity.getIban());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when entity is null")
    void testToDTO_NullEntity() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ModelUtil.toDTO(null)
        );
        assertEquals("Given object cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when DTO is null")
    void testToEntity_NullDTO() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ModelUtil.toEntity(null)
        );
        assertEquals("Given object cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when object is not from model package")
    void testToDTO_InvalidPackage() {
        // Given - Create an object from wrong package
        String invalidObject = "This is not from model package";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ModelUtil.toDTO(invalidObject)
        );
        assertEquals("Given objects must come from model package", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when object is not from dto package")
    void testToEntity_InvalidPackage() {
        // Given - Create an object from wrong package (using User entity instead of DTO)
        User invalidObject = new User();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ModelUtil.toEntity(invalidObject)
        );
        assertEquals("Given objects must come from dto package", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle User with null fields gracefully")
    void testToDTO_WithNullFields() {
        // Given - User with some null fields
        User userWithNulls = new User();
        userWithNulls.setId(null);
        userWithNulls.setUsername("test_user");
        userWithNulls.setEmail(null);
        userWithNulls.setRole(UserRole.USER);

        // When
        UserDTO resultDTO = ModelUtil.toDTO(userWithNulls);

        // Then
        assertNotNull(resultDTO);
        assertNull(resultDTO.getId());
        assertEquals("test_user", resultDTO.getUsername());
        assertNull(resultDTO.getEmail());
        assertEquals(UserRole.USER, resultDTO.getRole());
    }

    @Test
    @DisplayName("Should handle UserDTO with null fields gracefully")
    void testToEntity_WithNullFields() {
        // Given - UserDTO with some null fields
        UserDTO dtoWithNulls = new UserDTO();
        dtoWithNulls.setId(null);
        dtoWithNulls.setUsername("test_user");
        dtoWithNulls.setEmail(null);
        dtoWithNulls.setRole(UserRole.ADMIN);

        // When
        User resultEntity = ModelUtil.toEntity(dtoWithNulls);

        // Then
        assertNotNull(resultEntity);
        assertNull(resultEntity.getId());
        assertEquals("test_user", resultEntity.getUsername());
        assertNull(resultEntity.getEmail());
        assertEquals(UserRole.ADMIN, resultEntity.getRole());
    }
}