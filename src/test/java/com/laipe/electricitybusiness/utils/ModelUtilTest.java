package com.laipe.electricitybusiness.utils;

import com.laipe.electricitybusiness.model.AnotherTestModel;
import com.laipe.electricitybusiness.model.TestModel;
import com.laipe.electricitybusiness.model.TestModelWithRelation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe ModelUtil
 * Utilise des modèles factices du package model pour être totalement indépendant
 */
class ModelUtilTest {

    /**
     * Classe ne faisant pas partie du package model
     * Pour tester le rejet des objets hors package
     */
    static class NonModelClass {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    // ==================== Tests ====================

    @Nested
    @DisplayName("Tests de validation des objets")
    class ValidationTests {

        @Test
        @DisplayName("Devrait lancer une exception si source est null")
        void shouldThrowException_WhenSourceIsNull() {
            // Given
            TestModel destination = new TestModel();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ModelUtil.copyFields(null, destination));
            assertEquals("Given objects cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si destination est null")
        void shouldThrowException_WhenDestinationIsNull() {
            // Given
            TestModel source = new TestModel();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ModelUtil.copyFields(source, null));
            assertEquals("Given objects cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si les deux objets sont null")
        void shouldThrowException_WhenBothObjectsAreNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ModelUtil.copyFields(null, null));
            assertEquals("Given objects cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si source n'est pas du package model")
        void shouldThrowException_WhenSourceIsNotFromModelPackage() {
            // Given
            NonModelClass source = new NonModelClass();
            TestModel destination = new TestModel();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ModelUtil.copyFields(source, destination));
            assertEquals("Given objects must come from model package", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si destination n'est pas du package model")
        void shouldThrowException_WhenDestinationIsNotFromModelPackage() {
            // Given
            TestModel source = new TestModel();
            NonModelClass destination = new NonModelClass();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ModelUtil.copyFields(source, destination));
            assertEquals("Given objects must come from model package", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si les types source et destination sont différents")
        void shouldThrowException_WhenSourceAndDestinationTypesAreDifferent() {
            // Given
            TestModel source = new TestModel();
            AnotherTestModel destination = new AnotherTestModel();

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> ModelUtil.copyFields(source, destination));
            assertEquals("Source and Destination entities types are not the same", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests de copie des champs simples")
    class SimpleCopyTests {

        @Test
        @DisplayName("Devrait copier tous les champs non-null d'un modèle simple")
        void shouldCopyAllNonNullFields() {
            // Given
            TestModel source = new TestModel();
            source.setId(1L);
            source.setName("Test Name");
            source.setDescription("Test Description");
            source.setQuantity(42);

            TestModel destination = new TestModel();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertNotEquals(source.getId(), destination.getId(), "L'ID ne devrait pas être copié");
            assertNull(destination.getId(), "L'ID de destination devrait rester null");
            assertEquals(source.getName(), destination.getName());
            assertEquals(source.getDescription(), destination.getDescription());
            assertEquals(source.getQuantity(), destination.getQuantity());
        }

        @Test
        @DisplayName("Ne devrait pas copier l'ID")
        void shouldNotCopyId() {
            // Given
            TestModel source = new TestModel();
            source.setId(100L);
            source.setName("Test");

            TestModel destination = new TestModel();
            destination.setId(200L);

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals(200L, destination.getId(), "L'ID de destination ne devrait pas changer");
            assertNotEquals(source.getId(), destination.getId());
        }

        @Test
        @DisplayName("Ne devrait pas écraser les champs existants avec null")
        void shouldNotOverwriteWithNull() {
            // Given
            TestModel source = new TestModel();
            source.setName("New Name");
            // description est null dans source
            // quantity est null dans source

            TestModel destination = new TestModel();
            destination.setName("Old Name");
            destination.setDescription("Existing Description");
            destination.setQuantity(99);

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals("New Name", destination.getName(), "Devrait copier la valeur non-null");
            assertEquals("Existing Description", destination.getDescription(), "Ne devrait pas écraser avec null");
            assertEquals(99, destination.getQuantity(), "Ne devrait pas écraser avec null");
        }

        @Test
        @DisplayName("Devrait gérer la copie entre deux objets vides")
        void shouldHandleCopyBetweenEmptyObjects() {
            // Given
            TestModel source = new TestModel();
            TestModel destination = new TestModel();

            // When & Then
            assertDoesNotThrow(() -> ModelUtil.copyFields(source, destination));
        }
    }

    @Nested
    @DisplayName("Tests de copie avec relations")
    class RelationCopyTests {

        @Test
        @DisplayName("Devrait copier tous les champs non-null incluant les relations")
        void shouldCopyAllNonNullFieldsWithRelations() {
            // Given
            TestModel relatedModel = new TestModel();
            relatedModel.setId(10L);
            relatedModel.setName("Related");

            TestModelWithRelation source = new TestModelWithRelation();
            source.setId(1L);
            source.setCode("CODE-123");
            source.setPrice(99.99);
            source.setRelatedModel(relatedModel);

            TestModelWithRelation destination = new TestModelWithRelation();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertNull(destination.getId(), "L'ID ne devrait pas être copié");
            assertEquals(source.getCode(), destination.getCode());
            assertEquals(source.getPrice(), destination.getPrice());
            assertEquals(source.getRelatedModel(), destination.getRelatedModel());
        }

        @Test
        @DisplayName("Devrait copier correctement les types primitifs wrapper (Double, Integer)")
        void shouldCopyWrapperTypes() {
            // Given
            TestModelWithRelation source = new TestModelWithRelation();
            source.setPrice(149.99);

            TestModel sourceSimple = new TestModel();
            sourceSimple.setQuantity(25);

            TestModelWithRelation destination = new TestModelWithRelation();
            TestModel destinationSimple = new TestModel();

            // When
            ModelUtil.copyFields(source, destination);
            ModelUtil.copyFields(sourceSimple, destinationSimple);

            // Then
            assertEquals(149.99, destination.getPrice(), 0.001);
            assertEquals(25, destinationSimple.getQuantity());
        }

        @Test
        @DisplayName("Devrait copier des objets (relations) en copiant la référence")
        void shouldCopyObjectReferences() {
            // Given
            TestModel relatedModel = new TestModel();
            relatedModel.setId(10L);
            relatedModel.setName("Related Object");

            TestModelWithRelation source = new TestModelWithRelation();
            source.setRelatedModel(relatedModel);

            TestModelWithRelation destination = new TestModelWithRelation();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertNotNull(destination.getRelatedModel());
            assertEquals(10L, destination.getRelatedModel().getId());
            assertEquals("Related Object", destination.getRelatedModel().getName());
            assertSame(relatedModel, destination.getRelatedModel(), "Devrait copier la référence");
        }
    }

    @Nested
    @DisplayName("Tests avec différents types de données")
    class DataTypesTests {

        @Test
        @DisplayName("Devrait copier des Strings")
        void shouldCopyStrings() {
            // Given
            TestModel source = new TestModel();
            source.setName("Test Name");
            source.setDescription("Test Description");

            TestModel destination = new TestModel();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals("Test Name", destination.getName());
            assertEquals("Test Description", destination.getDescription());
        }

        @Test
        @DisplayName("Devrait copier des Integers")
        void shouldCopyIntegers() {
            // Given
            TestModel source = new TestModel();
            source.setQuantity(42);

            TestModel destination = new TestModel();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals(42, destination.getQuantity());
        }

        @Test
        @DisplayName("Devrait copier des Doubles")
        void shouldCopyDoubles() {
            // Given
            TestModelWithRelation source = new TestModelWithRelation();
            source.setPrice(123.45);

            TestModelWithRelation destination = new TestModelWithRelation();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals(123.45, destination.getPrice(), 0.001);
        }
    }

    @Nested
    @DisplayName("Tests de cas limites")
    class EdgeCaseTests {

        @Test
        @DisplayName("Devrait gérer un objet source avec tous les champs à null")
        void shouldHandleSourceWithAllNullFields() {
            // Given
            TestModel source = new TestModel();
            // Tous les champs sont null

            TestModel destination = new TestModel();
            destination.setName("EXISTING");
            destination.setDescription("existing-description");
            destination.setQuantity(50);

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals("EXISTING", destination.getName(), "Ne devrait pas écraser avec null");
            assertEquals("existing-description", destination.getDescription(), "Ne devrait pas écraser avec null");
            assertEquals(50, destination.getQuantity(), "Ne devrait pas écraser avec null");
        }

        @Test
        @DisplayName("Devrait gérer un objet destination avec tous les champs à null")
        void shouldHandleDestinationWithAllNullFields() {
            // Given
            TestModel source = new TestModel();
            source.setName("NEW");
            source.setDescription("new-description");
            source.setQuantity(100);

            TestModel destination = new TestModel();
            // Tous les champs sont null

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals("NEW", destination.getName());
            assertEquals("new-description", destination.getDescription());
            assertEquals(100, destination.getQuantity());
        }

        @Test
        @DisplayName("Devrait gérer la copie avec seulement l'ID défini")
        void shouldHandleCopyWithOnlyIdSet() {
            // Given
            TestModel source = new TestModel();
            source.setId(999L);

            TestModel destination = new TestModel();
            destination.setId(111L);

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals(111L, destination.getId(), "L'ID ne devrait jamais être copié");
        }

        @Test
        @DisplayName("Devrait gérer plusieurs copies successives")
        void shouldHandleMultipleCopies() {
            // Given
            TestModel source1 = new TestModel();
            source1.setName("Name 1");

            TestModel source2 = new TestModel();
            source2.setDescription("Description 2");

            TestModel destination = new TestModel();

            // When
            ModelUtil.copyFields(source1, destination);
            ModelUtil.copyFields(source2, destination);

            // Then
            assertEquals("Name 1", destination.getName(), "Devrait garder le premier champ copié");
            assertEquals("Description 2", destination.getDescription(), "Devrait avoir le second champ copié");
        }

        @Test
        @DisplayName("Devrait gérer la copie avec valeurs nulles mélangées")
        void shouldHandleMixedNullValues() {
            // Given
            TestModel source = new TestModel();
            source.setName("Only Name");
            // description et quantity sont null

            TestModel destination = new TestModel();
            destination.setDescription("Only Description");
            destination.setQuantity(75);
            // name est null

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals("Only Name", destination.getName(), "Devrait copier le nom");
            assertEquals("Only Description", destination.getDescription(), "Ne devrait pas écraser avec null");
            assertEquals(75, destination.getQuantity(), "Ne devrait pas écraser avec null");
        }
    }

    @Nested
    @DisplayName("Tests d'intégrité et de cohérence")
    class IntegrityTests {

        @Test
        @DisplayName("La copie ne devrait pas modifier l'objet source")
        void shouldNotModifySourceObject() {
            // Given
            TestModel source = new TestModel();
            source.setId(1L);
            source.setName("SOURCE");
            source.setDescription("SOURCE-DESC");
            source.setQuantity(10);

            TestModel destination = new TestModel();
            destination.setName("DEST");

            // When
            ModelUtil.copyFields(source, destination);

            // Then - Source doit rester inchangé
            assertEquals(1L, source.getId());
            assertEquals("SOURCE", source.getName());
            assertEquals("SOURCE-DESC", source.getDescription());
            assertEquals(10, source.getQuantity());
        }

        @Test
        @DisplayName("Les objets source et destination doivent rester des instances différentes")
        void shouldKeepSourceAndDestinationAsSeparateInstances() {
            // Given
            TestModel source = new TestModel();
            source.setName("SOURCE");

            TestModel destination = new TestModel();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertNotSame(source, destination, "Source et destination doivent être des instances différentes");

            // Modification de destination ne doit pas affecter source
            destination.setName("MODIFIED");
            assertEquals("SOURCE", source.getName());
            assertEquals("MODIFIED", destination.getName());
        }

        @Test
        @DisplayName("Devrait copier correctement les références sans créer de nouveaux objets")
        void shouldCopyReferencesWithoutCreatingNewObjects() {
            // Given
            TestModel relatedModel = new TestModel();
            relatedModel.setId(1L);
            relatedModel.setName("Related");

            TestModelWithRelation source = new TestModelWithRelation();
            source.setRelatedModel(relatedModel);

            TestModelWithRelation destination = new TestModelWithRelation();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertSame(relatedModel, destination.getRelatedModel(), "La référence devrait être copiée, pas clonée");
        }
    }

    @Nested
    @DisplayName("Tests de scénarios réalistes")
    class RealisticScenariosTests {

        @Test
        @DisplayName("Scénario: Mise à jour d'un objet existant avec de nouvelles données")
        void shouldUpdateExistingObjectWithNewData() {
            // Given - Objet existant en base
            TestModel existing = new TestModel();
            existing.setId(100L);
            existing.setName("Old Name");
            existing.setDescription("Old Description");
            existing.setQuantity(10);

            // Given - Nouvelles données reçues (sans ID)
            TestModel updatedData = new TestModel();
            updatedData.setName("New Name");
            updatedData.setDescription("New Description");
            updatedData.setQuantity(20);

            // When
            ModelUtil.copyFields(updatedData, existing);

            // Then
            assertEquals(100L, existing.getId(), "L'ID ne doit pas changer");
            assertEquals("New Name", existing.getName());
            assertEquals("New Description", existing.getDescription());
            assertEquals(20, existing.getQuantity());
        }

        @Test
        @DisplayName("Scénario: Mise à jour partielle d'un objet")
        void shouldPartiallyUpdateExistingObject() {
            // Given - Objet existant
            TestModelWithRelation existing = new TestModelWithRelation();
            existing.setId(50L);
            existing.setCode("CODE-A1");
            existing.setPrice(99.99);

            // Given - Mise à jour du prix uniquement
            TestModelWithRelation update = new TestModelWithRelation();
            update.setPrice(149.99);

            // When
            ModelUtil.copyFields(update, existing);

            // Then
            assertEquals(50L, existing.getId());
            assertEquals("CODE-A1", existing.getCode(), "Le code ne devrait pas changer");
            assertEquals(149.99, existing.getPrice(), 0.001, "Le prix devrait être mis à jour");
        }

        @Test
        @DisplayName("Scénario: Clonage d'un objet (sans l'ID)")
        void shouldCloneObjectWithoutId() {
            // Given
            TestModel original = new TestModel();
            original.setId(1L);
            original.setName("Original Name");
            original.setDescription("Original Description");
            original.setQuantity(50);

            TestModel clone = new TestModel();

            // When
            ModelUtil.copyFields(original, clone);

            // Then
            assertNull(clone.getId(), "Le clone ne devrait pas avoir d'ID");
            assertEquals(original.getName(), clone.getName());
            assertEquals(original.getDescription(), clone.getDescription());
            assertEquals(original.getQuantity(), clone.getQuantity());
        }

        @Test
        @DisplayName("Scénario: Mise à jour avec relation")
        void shouldUpdateWithRelation() {
            // Given - Objet existant avec relation
            TestModel oldRelation = new TestModel();
            oldRelation.setId(1L);
            oldRelation.setName("Old Relation");

            TestModelWithRelation existing = new TestModelWithRelation();
            existing.setId(100L);
            existing.setCode("OLD-CODE");
            existing.setRelatedModel(oldRelation);

            // Given - Nouvelle relation
            TestModel newRelation = new TestModel();
            newRelation.setId(2L);
            newRelation.setName("New Relation");

            TestModelWithRelation update = new TestModelWithRelation();
            update.setCode("NEW-CODE");
            update.setRelatedModel(newRelation);

            // When
            ModelUtil.copyFields(update, existing);

            // Then
            assertEquals(100L, existing.getId(), "L'ID ne doit pas changer");
            assertEquals("NEW-CODE", existing.getCode());
            assertEquals(newRelation, existing.getRelatedModel());
            assertEquals(2L, existing.getRelatedModel().getId());
        }
    }

    @Nested
    @DisplayName("Tests de robustesse")
    class RobustnessTests {

        @Test
        @DisplayName("Devrait gérer des valeurs extrêmes pour Integer")
        void shouldHandleExtremeIntegerValues() {
            // Given
            TestModel source = new TestModel();
            source.setQuantity(Integer.MAX_VALUE);

            TestModel destination = new TestModel();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals(Integer.MAX_VALUE, destination.getQuantity());
        }

        @Test
        @DisplayName("Devrait gérer des valeurs extrêmes pour Double")
        void shouldHandleExtremeDoubleValues() {
            // Given
            TestModelWithRelation source = new TestModelWithRelation();
            source.setPrice(Double.MAX_VALUE);

            TestModelWithRelation destination = new TestModelWithRelation();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals(Double.MAX_VALUE, destination.getPrice(), 0.001);
        }

        @Test
        @DisplayName("Devrait gérer des chaînes vides")
        void shouldHandleEmptyStrings() {
            // Given
            TestModel source = new TestModel();
            source.setName("");
            source.setDescription("");

            TestModel destination = new TestModel();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals("", destination.getName());
            assertEquals("", destination.getDescription());
        }

        @Test
        @DisplayName("Devrait gérer des chaînes très longues")
        void shouldHandleLongStrings() {
            // Given
            String longString = "A".repeat(10000);
            TestModel source = new TestModel();
            source.setName(longString);

            TestModel destination = new TestModel();

            // When
            ModelUtil.copyFields(source, destination);

            // Then
            assertEquals(longString, destination.getName());
            assertEquals(10000, destination.getName().length());
        }
    }
}

