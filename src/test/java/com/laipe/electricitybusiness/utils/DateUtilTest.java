package com.laipe.electricitybusiness.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe DateUtil
 */
class DateUtilTest {

    private DateUtil dateUtil;

    @BeforeEach
    void setUp() {
        dateUtil = new DateUtil();
    }

    @Nested
    @DisplayName("Tests de chevauchement d'intervalles")
    class OverlapTests {

        @Test
        @DisplayName("Devrait détecter un chevauchement partiel - début du premier dans le second")
        void shouldDetectPartialOverlap_StartOfFirstInSecond() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 12, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 11, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 13, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait détecter un chevauchement partiel - fin du premier dans le second")
        void shouldDetectPartialOverlap_EndOfFirstInSecond() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 11, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 13, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 12, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait détecter un chevauchement total - premier complètement dans le second")
        void shouldDetectTotalOverlap_FirstCompletelyInSecond() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 11, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 12, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 13, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait détecter un chevauchement total - second complètement dans le premier")
        void shouldDetectTotalOverlap_SecondCompletelyInFirst() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 13, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 11, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 12, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait détecter un chevauchement - intervalles identiques")
        void shouldDetectOverlap_IdenticalIntervals() {
            // Given
            LocalDateTime start = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end = LocalDateTime.of(2025, 11, 20, 12, 0);

            // When
            boolean result = dateUtil.doOverlap(start, end, start, end);

            // Then
            assertTrue(result, "Les intervalles identiques devraient se chevaucher");
        }

        @Test
        @DisplayName("Ne devrait pas détecter de chevauchement - intervalles consécutifs (fin1 = start2)")
        void shouldNotDetectOverlap_ConsecutiveIntervals() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 12, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 12, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 14, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertFalse(result, "Les intervalles consécutifs ne devraient pas se chevaucher");
        }

        @Test
        @DisplayName("Ne devrait pas détecter de chevauchement - intervalles séparés (premier avant le second)")
        void shouldNotDetectOverlap_SeparatedIntervals_FirstBeforeSecond() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 11, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 13, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 14, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertFalse(result, "Les intervalles séparés ne devraient pas se chevaucher");
        }

        @Test
        @DisplayName("Ne devrait pas détecter de chevauchement - intervalles séparés (second avant le premier)")
        void shouldNotDetectOverlap_SeparatedIntervals_SecondBeforeFirst() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 13, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 14, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 11, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertFalse(result, "Les intervalles séparés ne devraient pas se chevaucher");
        }
    }

    @Nested
    @DisplayName("Tests de cas limites")
    class EdgeCaseTests {

        @Test
        @DisplayName("Devrait gérer un intervalle d'une minute")
        void shouldHandleOneMinuteInterval() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 10, 1);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 10, 1);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles d'une minute devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait gérer un chevauchement d'une seconde")
        void shouldHandleOneSecondOverlap() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 10, 0, 2);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 10, 0, 1);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 10, 0, 3);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles avec un chevauchement d'une seconde devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait gérer des intervalles sur plusieurs jours")
        void shouldHandleMultiDayIntervals() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 22, 10, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 21, 10, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 23, 10, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles de plusieurs jours devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait gérer des intervalles sur plusieurs mois")
        void shouldHandleMultiMonthIntervals() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 10, 1, 0, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 12, 31, 23, 59);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 1, 0, 0);
            LocalDateTime end2 = LocalDateTime.of(2026, 1, 31, 23, 59);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles de plusieurs mois devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait gérer un chevauchement au changement d'année")
        void shouldHandleOverlapAcrossYearChange() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 12, 31, 23, 0);
            LocalDateTime end1 = LocalDateTime.of(2026, 1, 1, 1, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 12, 31, 22, 0);
            LocalDateTime end2 = LocalDateTime.of(2026, 1, 1, 0, 0);

            // When
            boolean result = dateUtil.doOverlap(start1, end1, start2, end2);

            // Then
            assertTrue(result, "Les intervalles au changement d'année devraient se chevaucher");
        }
    }

    @Nested
    @DisplayName("Tests de symétrie")
    class SymmetryTests {

        @Test
        @DisplayName("Devrait être symétrique - ordre des paramètres ne devrait pas importer")
        void shouldBeSymmetric_OverlappingIntervals() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 12, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 11, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 13, 0);

            // When
            boolean result1 = dateUtil.doOverlap(start1, end1, start2, end2);
            boolean result2 = dateUtil.doOverlap(start2, end2, start1, end1);

            // Then
            assertEquals(result1, result2, "La méthode devrait être symétrique");
            assertTrue(result1, "Les intervalles devraient se chevaucher");
        }

        @Test
        @DisplayName("Devrait être symétrique - intervalles non chevauchants")
        void shouldBeSymmetric_NonOverlappingIntervals() {
            // Given
            LocalDateTime start1 = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime end1 = LocalDateTime.of(2025, 11, 20, 11, 0);
            LocalDateTime start2 = LocalDateTime.of(2025, 11, 20, 12, 0);
            LocalDateTime end2 = LocalDateTime.of(2025, 11, 20, 13, 0);

            // When
            boolean result1 = dateUtil.doOverlap(start1, end1, start2, end2);
            boolean result2 = dateUtil.doOverlap(start2, end2, start1, end1);

            // Then
            assertEquals(result1, result2, "La méthode devrait être symétrique");
            assertFalse(result1, "Les intervalles ne devraient pas se chevaucher");
        }
    }
}

