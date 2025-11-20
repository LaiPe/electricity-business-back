package com.laipe.electricitybusiness.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe GeolocatorUtil
 */
class GeolocatorUtilTest {

    private GeolocatorUtil geolocatorUtil;

    @BeforeEach
    void setUp() {
        geolocatorUtil = new GeolocatorUtil();
    }

    @Nested
    @DisplayName("Tests de validation des coordonnées")
    class ValidationTests {

        @Test
        @DisplayName("Devrait lancer une exception si lat1 est null")
        void shouldThrowException_WhenLat1IsNull() {
            // Given
            BigDecimal lon1 = new BigDecimal("2.3522");
            BigDecimal lat2 = new BigDecimal("48.8566");
            BigDecimal lon2 = new BigDecimal("2.3522");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> geolocatorUtil.calculateDistance(null, lon1, lat2, lon2));
            assertEquals("Les coordonnées ne peuvent pas être null", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si lon1 est null")
        void shouldThrowException_WhenLon1IsNull() {
            // Given
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lat2 = new BigDecimal("48.8566");
            BigDecimal lon2 = new BigDecimal("2.3522");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> geolocatorUtil.calculateDistance(lat1, null, lat2, lon2));
            assertEquals("Les coordonnées ne peuvent pas être null", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si lat2 est null")
        void shouldThrowException_WhenLat2IsNull() {
            // Given
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            BigDecimal lon2 = new BigDecimal("2.3522");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> geolocatorUtil.calculateDistance(lat1, lon1, null, lon2));
            assertEquals("Les coordonnées ne peuvent pas être null", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si lon2 est null")
        void shouldThrowException_WhenLon2IsNull() {
            // Given
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            BigDecimal lat2 = new BigDecimal("48.8566");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> geolocatorUtil.calculateDistance(lat1, lon1, lat2, null));
            assertEquals("Les coordonnées ne peuvent pas être null", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait lancer une exception si toutes les coordonnées sont null")
        void shouldThrowException_WhenAllCoordinatesAreNull() {
            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> geolocatorUtil.calculateDistance(null, null, null, null));
            assertEquals("Les coordonnées ne peuvent pas être null", exception.getMessage());
        }

        @ParameterizedTest
        @CsvSource({
                "-91, 0",
                "91, 0",
                "-90.1, 0",
                "90.1, 0",
                "-100, 0",
                "100, 0"
        })
        @DisplayName("Devrait lancer une exception si lat1 est hors limites")
        void shouldThrowException_WhenLat1IsOutOfBounds(String lat, String lon) {
            // Given
            BigDecimal lat1 = new BigDecimal(lat);
            BigDecimal lon1 = new BigDecimal(lon);
            BigDecimal lat2 = new BigDecimal("48.8566");
            BigDecimal lon2 = new BigDecimal("2.3522");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2));
            assertEquals("La latitude doit être comprise entre -90 et 90", exception.getMessage());
        }

        @ParameterizedTest
        @CsvSource({
                "48.8566, -181",
                "48.8566, 181",
                "48.8566, -180.1",
                "48.8566, 180.1",
                "48.8566, -200",
                "48.8566, 200"
        })
        @DisplayName("Devrait lancer une exception si lon1 est hors limites")
        void shouldThrowException_WhenLon1IsOutOfBounds(String lat, String lon) {
            // Given
            BigDecimal lat1 = new BigDecimal(lat);
            BigDecimal lon1 = new BigDecimal(lon);
            BigDecimal lat2 = new BigDecimal("48.8566");
            BigDecimal lon2 = new BigDecimal("2.3522");

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2));
            assertEquals("La longitude doit être comprise entre -180 et 180", exception.getMessage());
        }

        @Test
        @DisplayName("Devrait accepter les limites valides de latitude (-90, 90)")
        void shouldAcceptValidLatitudeBounds() {
            // Given
            BigDecimal lat1 = new BigDecimal("-90");
            BigDecimal lon1 = new BigDecimal("0");
            BigDecimal lat2 = new BigDecimal("90");
            BigDecimal lon2 = new BigDecimal("0");

            // When & Then
            assertDoesNotThrow(() -> geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2));
        }

        @Test
        @DisplayName("Devrait accepter les limites valides de longitude (-180, 180)")
        void shouldAcceptValidLongitudeBounds() {
            // Given
            BigDecimal lat1 = new BigDecimal("0");
            BigDecimal lon1 = new BigDecimal("-180");
            BigDecimal lat2 = new BigDecimal("0");
            BigDecimal lon2 = new BigDecimal("180");

            // When & Then
            assertDoesNotThrow(() -> geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2));
        }
    }

    @Nested
    @DisplayName("Tests de calcul de distance avec des villes réelles")
    class RealWorldDistanceTests {

        @Test
        @DisplayName("Devrait calculer la distance Paris - Londres (environ 344 km)")
        void shouldCalculateDistance_ParisToLondon() {
            // Given - Paris
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            // Londres
            BigDecimal lat2 = new BigDecimal("51.5074");
            BigDecimal lon2 = new BigDecimal("-0.1278");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then - La distance réelle est d'environ 344 km
            assertEquals(344, distance, 5, "La distance Paris-Londres devrait être d'environ 344 km");
        }

        @Test
        @DisplayName("Devrait calculer la distance Paris - New York (environ 5837 km)")
        void shouldCalculateDistance_ParisToNewYork() {
            // Given - Paris
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            // New York
            BigDecimal lat2 = new BigDecimal("40.7128");
            BigDecimal lon2 = new BigDecimal("-74.0060");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then - La distance réelle est d'environ 5837 km
            assertEquals(5837, distance, 10, "La distance Paris-New York devrait être d'environ 5837 km");
        }

        @Test
        @DisplayName("Devrait calculer la distance Paris - Tokyo (environ 9714 km)")
        void shouldCalculateDistance_ParisToTokyo() {
            // Given - Paris
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            // Tokyo
            BigDecimal lat2 = new BigDecimal("35.6762");
            BigDecimal lon2 = new BigDecimal("139.6503");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then - La distance réelle est d'environ 9714 km
            assertEquals(9714, distance, 15, "La distance Paris-Tokyo devrait être d'environ 9714 km");
        }

        @Test
        @DisplayName("Devrait calculer la distance Sydney - Los Angeles (environ 12051 km)")
        void shouldCalculateDistance_SydneyToLosAngeles() {
            // Given - Sydney
            BigDecimal lat1 = new BigDecimal("-33.8671");
            BigDecimal lon1 = new BigDecimal("151.2071");
            // Los Angeles
            BigDecimal lat2 = new BigDecimal("34.0522");
            BigDecimal lon2 = new BigDecimal("-118.2437");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then - La distance réelle est d'environ 12073 km
            assertEquals(12073, distance, 20, "La distance Sydney-Los Angeles devrait être d'environ 12051 km");
        }
    }

    @Nested
    @DisplayName("Tests de cas particuliers")
    class SpecialCaseTests {

        @Test
        @DisplayName("Devrait retourner 0 pour deux points identiques")
        void shouldReturnZero_ForIdenticalPoints() {
            // Given
            BigDecimal lat = new BigDecimal("48.8566");
            BigDecimal lon = new BigDecimal("2.3522");

            // When
            double distance = geolocatorUtil.calculateDistance(lat, lon, lat, lon);

            // Then
            assertEquals(0, distance, 0.001, "La distance entre deux points identiques devrait être 0");
        }

        @Test
        @DisplayName("Devrait calculer la distance à l'équateur")
        void shouldCalculateDistance_AtEquator() {
            // Given
            BigDecimal lat1 = new BigDecimal("0");
            BigDecimal lon1 = new BigDecimal("0");
            BigDecimal lat2 = new BigDecimal("0");
            BigDecimal lon2 = new BigDecimal("1");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then - 1 degré de longitude à l'équateur = environ 111 km
            assertEquals(111.19, distance, 0.5, "1 degré de longitude à l'équateur devrait être environ 111 km");
        }

        @Test
        @DisplayName("Devrait calculer la distance entre les pôles Nord et Sud")
        void shouldCalculateDistance_BetweenPoles() {
            // Given - Pôle Nord
            BigDecimal lat1 = new BigDecimal("90");
            BigDecimal lon1 = new BigDecimal("0");
            // Pôle Sud
            BigDecimal lat2 = new BigDecimal("-90");
            BigDecimal lon2 = new BigDecimal("0");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then - La distance devrait être environ la moitié de la circonférence terrestre (20015 km)
            assertEquals(20015, distance, 10, "La distance entre les pôles devrait être environ 20015 km");
        }

        @Test
        @DisplayName("Devrait calculer la distance en traversant l'antiméridien (ligne de date)")
        void shouldCalculateDistance_AcrossAntimeridian() {
            // Given - Point à l'est de l'antiméridien
            BigDecimal lat1 = new BigDecimal("0");
            BigDecimal lon1 = new BigDecimal("179");
            // Point à l'ouest de l'antiméridien
            BigDecimal lat2 = new BigDecimal("0");
            BigDecimal lon2 = new BigDecimal("-179");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then - La distance devrait être environ 222 km (2 degrés à l'équateur)
            assertEquals(222.39, distance, 1, "La distance devrait être environ 222 km");
        }

        @Test
        @DisplayName("Devrait gérer des coordonnées avec beaucoup de décimales")
        void shouldHandleHighPrecisionCoordinates() {
            // Given
            BigDecimal lat1 = new BigDecimal("48.85661");
            BigDecimal lon1 = new BigDecimal("2.35222");
            BigDecimal lat2 = new BigDecimal("48.85662");
            BigDecimal lon2 = new BigDecimal("2.35223");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then - La distance devrait être très petite (quelques mètres)
            assertTrue(distance < 0.02, "La distance devrait être inférieure à 20 mètres");
            assertTrue(distance > 0, "La distance devrait être supérieure à 0");
        }
    }

    @Nested
    @DisplayName("Tests de symétrie et propriétés mathématiques")
    class MathematicalPropertiesTests {

        @Test
        @DisplayName("Devrait être symétrique - distance(A,B) = distance(B,A)")
        void shouldBeSymmetric() {
            // Given - Paris et Londres
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            BigDecimal lat2 = new BigDecimal("51.5074");
            BigDecimal lon2 = new BigDecimal("-0.1278");

            // When
            double distance1 = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);
            double distance2 = geolocatorUtil.calculateDistance(lat2, lon2, lat1, lon1);

            // Then
            assertEquals(distance1, distance2, 0.001, "La distance devrait être symétrique");
        }

        @Test
        @DisplayName("Devrait retourner une distance positive")
        void shouldReturnPositiveDistance() {
            // Given
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            BigDecimal lat2 = new BigDecimal("51.5074");
            BigDecimal lon2 = new BigDecimal("-0.1278");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then
            assertTrue(distance >= 0, "La distance devrait toujours être positive ou nulle");
        }

        @Test
        @DisplayName("Devrait respecter l'inégalité triangulaire")
        void shouldRespectTriangleInequality() {
            // Given - Paris, Londres, Berlin
            BigDecimal latParis = new BigDecimal("48.8566");
            BigDecimal lonParis = new BigDecimal("2.3522");
            BigDecimal latLondon = new BigDecimal("51.5074");
            BigDecimal lonLondon = new BigDecimal("-0.1278");
            BigDecimal latBerlin = new BigDecimal("52.5200");
            BigDecimal lonBerlin = new BigDecimal("13.4050");

            // When
            double distanceParisLondon = geolocatorUtil.calculateDistance(latParis, lonParis, latLondon, lonLondon);
            double distanceLondonBerlin = geolocatorUtil.calculateDistance(latLondon, lonLondon, latBerlin, lonBerlin);
            double distanceParisBerlin = geolocatorUtil.calculateDistance(latParis, lonParis, latBerlin, lonBerlin);

            // Then - distance(Paris,Berlin) <= distance(Paris,London) + distance(London,Berlin)
            assertTrue(distanceParisBerlin <= distanceParisLondon + distanceLondonBerlin + 1,
                    "L'inégalité triangulaire devrait être respectée");
        }
    }

    @Nested
    @DisplayName("Tests de distances courtes")
    class ShortDistanceTests {

        @Test
        @DisplayName("Devrait calculer une distance de 1 km avec précision")
        void shouldCalculateShortDistance_1km() {
            // Given - Deux points proches (environ 1 km)
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            BigDecimal lat2 = new BigDecimal("48.8656");
            BigDecimal lon2 = new BigDecimal("2.3522");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then
            assertEquals(1, distance, 0.1, "La distance devrait être environ 1 km");
        }

        @Test
        @DisplayName("Devrait calculer une distance de 100 mètres avec précision")
        void shouldCalculateShortDistance_100m() {
            // Given - Deux points très proches (environ 100m)
            BigDecimal lat1 = new BigDecimal("48.8566");
            BigDecimal lon1 = new BigDecimal("2.3522");
            BigDecimal lat2 = new BigDecimal("48.8575");
            BigDecimal lon2 = new BigDecimal("2.3522");

            // When
            double distance = geolocatorUtil.calculateDistance(lat1, lon1, lat2, lon2);

            // Then
            assertEquals(0.1, distance, 0.01, "La distance devrait être environ 0.1 km (100m)");
        }
    }
}

