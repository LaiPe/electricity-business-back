package com.laipe.electricitybusiness.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe PowerCalculatorUtil
 */
class PowerCalculatorUtilTest {

    private PowerCalculatorUtil powerCalculatorUtil;

    @BeforeEach
    void setUp() {
        powerCalculatorUtil = new PowerCalculatorUtil();
    }

    @Nested
    @DisplayName("Tests de calcul de la puissance consommée")
    class ConsumedPowerTests {

        @Test
        @DisplayName("Devrait calculer la consommation pour 1 heure à 10 kW")
        void shouldCalculateConsumedPower_1Hour_10kW() {
            // Given
            double power = 10.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 11, 0);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(10.0, consumedPower, 0.001, "1h à 10 kW devrait consommer 10 kWh");
        }

        @Test
        @DisplayName("Devrait calculer la consommation pour 2 heures à 7.5 kW")
        void shouldCalculateConsumedPower_2Hours_7_5kW() {
            // Given
            double power = 7.5;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 12, 0);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(15.0, consumedPower, 0.001, "2h à 7.5 kW devrait consommer 15 kWh");
        }

        @Test
        @DisplayName("Devrait calculer la consommation pour 30 minutes à 22 kW")
        void shouldCalculateConsumedPower_30Minutes_22kW() {
            // Given
            double power = 22.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 10, 30);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(11.0, consumedPower, 0.001, "30min à 22 kW devrait consommer 11 kWh");
        }

        @Test
        @DisplayName("Devrait calculer la consommation pour 15 minutes à 50 kW")
        void shouldCalculateConsumedPower_15Minutes_50kW() {
            // Given
            double power = 50.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 10, 15);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(12.5, consumedPower, 0.001, "15min à 50 kW devrait consommer 12.5 kWh");
        }

        @Test
        @DisplayName("Devrait calculer la consommation pour 3 heures et 45 minutes à 11 kW")
        void shouldCalculateConsumedPower_3Hours45Minutes_11kW() {
            // Given
            double power = 11.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 13, 45);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(41.25, consumedPower, 0.001, "3h45 à 11 kW devrait consommer 41.25 kWh");
        }

        @Test
        @DisplayName("Devrait calculer la consommation pour 1 minute à 120 kW (charge rapide)")
        void shouldCalculateConsumedPower_1Minute_120kW() {
            // Given
            double power = 120.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 10, 1);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(2.0, consumedPower, 0.001, "1min à 120 kW devrait consommer 2 kWh");
        }

        @Test
        @DisplayName("Devrait calculer la consommation pour 24 heures à 3.7 kW")
        void shouldCalculateConsumedPower_24Hours_3_7kW() {
            // Given
            double power = 3.7;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 21, 0, 0);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(88.8, consumedPower, 0.001, "24h à 3.7 kW devrait consommer 88.8 kWh");
        }

        @ParameterizedTest
        @CsvSource({
                "7.0, 1, 7.0",
                "11.0, 2, 22.0",
                "22.0, 3, 66.0",
                "50.0, 1, 50.0",
                "3.7, 5, 18.5"
        })
        @DisplayName("Devrait calculer la consommation pour différentes combinaisons puissance/durée")
        void shouldCalculateConsumedPower_ParameterizedTests(double power, int hours, double expectedConsumption) {
            // Given
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = startTime.plusHours(hours);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(expectedConsumption, consumedPower, 0.001);
        }
    }

    @Nested
    @DisplayName("Tests de calcul du coût")
    class CostCalculationTests {

        @Test
        @DisplayName("Devrait calculer le coût pour 10 kWh à 0.15 €/kWh")
        void shouldCalculateCost_10kWh_0_15EuroPerKWh() {
            // Given
            double consumedPower = 10.0;
            double pricePerKWh = 0.15;

            // When
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(1.50, cost, 0.001, "10 kWh à 0.15 €/kWh devrait coûter 1.50 €");
        }

        @Test
        @DisplayName("Devrait calculer le coût pour 50 kWh à 0.20 €/kWh")
        void shouldCalculateCost_50kWh_0_20EuroPerKWh() {
            // Given
            double consumedPower = 50.0;
            double pricePerKWh = 0.20;

            // When
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(10.0, cost, 0.001, "50 kWh à 0.20 €/kWh devrait coûter 10.0 €");
        }

        @Test
        @DisplayName("Devrait calculer le coût pour 25.5 kWh à 0.18 €/kWh")
        void shouldCalculateCost_25_5kWh_0_18EuroPerKWh() {
            // Given
            double consumedPower = 25.5;
            double pricePerKWh = 0.18;

            // When
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(4.59, cost, 0.001, "25.5 kWh à 0.18 €/kWh devrait coûter 4.59 €");
        }

        @ParameterizedTest
        @CsvSource({
                "10.0, 0.15, 1.50",
                "20.0, 0.20, 4.00",
                "100.0, 0.12, 12.00",
                "5.5, 0.25, 1.375",
                "75.0, 0.18, 13.50"
        })
        @DisplayName("Devrait calculer le coût pour différentes combinaisons consommation/prix")
        void shouldCalculateCost_ParameterizedTests(double consumedPower, double pricePerKWh, double expectedCost) {
            // When
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(expectedCost, cost, 0.001);
        }

        @Test
        @DisplayName("Devrait calculer le coût zéro pour une consommation nulle")
        void shouldCalculateCost_ZeroConsumption() {
            // Given
            double consumedPower = 0.0;
            double pricePerKWh = 0.15;

            // When
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(0.0, cost, 0.001, "Une consommation nulle devrait avoir un coût de 0");
        }

        @Test
        @DisplayName("Devrait calculer le coût zéro pour un prix nul")
        void shouldCalculateCost_ZeroPrice() {
            // Given
            double consumedPower = 10.0;
            double pricePerKWh = 0.0;

            // When
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(0.0, cost, 0.001, "Un prix nul devrait donner un coût de 0");
        }
    }

    @Nested
    @DisplayName("Tests de scénarios de recharge réalistes")
    class RealisticChargingScenarioTests {

        @Test
        @DisplayName("Scénario: Recharge domestique standard (3.7 kW pendant 8h)")
        void shouldCalculate_HomeCharging_3_7kW_8Hours() {
            // Given - Recharge domestique typique
            double power = 3.7;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 22, 0); // 22h
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 21, 6, 0);    // 6h le lendemain
            double pricePerKWh = 0.15;

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(29.6, consumedPower, 0.1, "8h à 3.7 kW = 29.6 kWh");
            assertEquals(4.44, cost, 0.01, "29.6 kWh à 0.15 €/kWh = 4.44 €");
        }

        @Test
        @DisplayName("Scénario: Recharge accélérée (11 kW pendant 3h)")
        void shouldCalculate_AcceleratedCharging_11kW_3Hours() {
            // Given - Wallbox 11 kW
            double power = 11.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 18, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 21, 0);
            double pricePerKWh = 0.18;

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(33.0, consumedPower, 0.1, "3h à 11 kW = 33 kWh");
            assertEquals(5.94, cost, 0.01, "33 kWh à 0.18 €/kWh = 5.94 €");
        }

        @Test
        @DisplayName("Scénario: Recharge rapide DC (50 kW pendant 30 minutes)")
        void shouldCalculate_FastCharging_50kW_30Minutes() {
            // Given - Borne rapide DC
            double power = 50.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 14, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 14, 30);
            double pricePerKWh = 0.40; // Prix plus élevé pour charge rapide

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(25.0, consumedPower, 0.1, "30min à 50 kW = 25 kWh");
            assertEquals(10.0, cost, 0.01, "25 kWh à 0.40 €/kWh = 10.0 €");
        }

        @Test
        @DisplayName("Scénario: Superchargeur Tesla (150 kW pendant 20 minutes)")
        void shouldCalculate_Supercharger_150kW_20Minutes() {
            // Given - Superchargeur
            double power = 150.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 12, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 12, 20);
            double pricePerKWh = 0.45;

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(50.0, consumedPower, 0.1, "20min à 150 kW = 50 kWh");
            assertEquals(22.5, cost, 0.01, "50 kWh à 0.45 €/kWh = 22.5 €");
        }

        @Test
        @DisplayName("Scénario: Recharge complète batterie 60 kWh avec borne 7.4 kW")
        void shouldCalculate_FullBatteryCharge_60kWh_7_4kW() {
            // Given - Recharge complète d'une batterie de 60 kWh
            double power = 7.4;
            // Durée pour 60 kWh à 7.4 kW = 60/7.4 = 8.1 heures ≈ 8h6min
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 20, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 21, 4, 6);
            double pricePerKWh = 0.16;

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(60.0, consumedPower, 0.5, "8.1h à 7.4 kW = 60 kWh");
            assertEquals(9.6, cost, 0.1, "60 kWh à 0.16 €/kWh = 9.6 €");
        }
    }

    @Nested
    @DisplayName("Tests de cas limites")
    class EdgeCaseTests {

        @Test
        @DisplayName("Devrait gérer une durée nulle (startTime = endTime)")
        void shouldHandle_ZeroDuration() {
            // Given
            double power = 10.0;
            LocalDateTime time = LocalDateTime.of(2025, 11, 20, 10, 0);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, time, time);

            // Then
            assertEquals(0.0, consumedPower, 0.001, "Une durée nulle devrait donner une consommation de 0");
        }

        @Test
        @DisplayName("Devrait gérer une puissance nulle")
        void shouldHandle_ZeroPower() {
            // Given
            double power = 0.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 11, 0);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(0.0, consumedPower, 0.001, "Une puissance nulle devrait donner une consommation de 0");
        }

        @Test
        @DisplayName("Devrait gérer une très courte durée (1 seconde)")
        void shouldHandle_OneSecondDuration() {
            // Given
            double power = 3600.0; // 3600 kW
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 10, 0, 1);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(1.0, consumedPower, 0.001, "1 seconde à 3600 kW devrait consommer 1 kWh");
        }

        @Test
        @DisplayName("Devrait gérer une très longue durée (1 mois)")
        void shouldHandle_OneMonthDuration() {
            // Given
            double power = 1.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 1, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 12, 1, 0, 0);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(720.0, consumedPower, 0.1, "30 jours à 1 kW devrait consommer 720 kWh");
        }

        @Test
        @DisplayName("Devrait gérer une très haute puissance (350 kW)")
        void shouldHandle_VeryHighPower() {
            // Given - Chargeurs ultra-rapides
            double power = 350.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 10, 10);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            assertEquals(58.333, consumedPower, 0.01, "10min à 350 kW devrait consommer ~58.3 kWh");
        }

        @Test
        @DisplayName("Devrait gérer des prix décimaux complexes")
        void shouldHandle_ComplexDecimalPrices() {
            // Given
            double consumedPower = 33.333;
            double pricePerKWh = 0.17658;

            // When
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(5.886, cost, 0.001);
        }
    }

    @Nested
    @DisplayName("Tests de cohérence et propriétés mathématiques")
    class MathematicalPropertiesTests {

        @Test
        @DisplayName("Devrait respecter la linéarité: 2x la durée = 2x la consommation")
        void shouldRespectLinearity_Duration() {
            // Given
            double power = 10.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime1 = LocalDateTime.of(2025, 11, 20, 11, 0); // 1h
            LocalDateTime endTime2 = LocalDateTime.of(2025, 11, 20, 12, 0); // 2h

            // When
            double consumption1h = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime1);
            double consumption2h = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime2);

            // Then
            assertEquals(consumption1h * 2, consumption2h, 0.001, "2x la durée devrait donner 2x la consommation");
        }

        @Test
        @DisplayName("Devrait respecter la linéarité: 2x la puissance = 2x la consommation")
        void shouldRespectLinearity_Power() {
            // Given
            double power1 = 10.0;
            double power2 = 20.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 11, 0);

            // When
            double consumption10kW = powerCalculatorUtil.calculateConsumedPower(power1, startTime, endTime);
            double consumption20kW = powerCalculatorUtil.calculateConsumedPower(power2, startTime, endTime);

            // Then
            assertEquals(consumption10kW * 2, consumption20kW, 0.001, "2x la puissance devrait donner 2x la consommation");
        }

        @Test
        @DisplayName("Devrait respecter la linéarité du coût")
        void shouldRespectLinearity_Cost() {
            // Given
            double consumedPower = 10.0;
            double price1 = 0.15;
            double price2 = 0.30;

            // When
            double cost1 = powerCalculatorUtil.calculateCost(consumedPower, price1);
            double cost2 = powerCalculatorUtil.calculateCost(consumedPower, price2);

            // Then
            assertEquals(cost1 * 2, cost2, 0.001, "2x le prix devrait donner 2x le coût");
        }

        @Test
        @DisplayName("Devrait vérifier que consommation = puissance × durée")
        void shouldVerify_ConsumptionFormula() {
            // Given
            double power = 22.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 13, 30); // 3.5h

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);
            double expectedConsumption = 22.0 * 3.5; // 77 kWh

            // Then
            assertEquals(expectedConsumption, consumedPower, 0.001, "Consommation = Puissance × Durée");
        }

        @Test
        @DisplayName("Devrait vérifier que coût = consommation × prix")
        void shouldVerify_CostFormula() {
            // Given
            double consumedPower = 45.5;
            double pricePerKWh = 0.22;

            // When
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);
            double expectedCost = 45.5 * 0.22; // 10.01 €

            // Then
            assertEquals(expectedCost, cost, 0.001, "Coût = Consommation × Prix");
        }
    }

    @Nested
    @DisplayName("Tests de précision")
    class PrecisionTests {

        @Test
        @DisplayName("Devrait calculer avec précision pour des fractions d'heure")
        void shouldCalculate_WithPrecision_FractionalHours() {
            // Given - 1h23min à 7.4 kW
            double power = 7.4;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 11, 23);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            // 1h23min = 1.3833h → 7.4 × 1.3833 = 10.236 kWh
            assertEquals(10.236, consumedPower, 0.01);
        }

        @Test
        @DisplayName("Devrait calculer avec précision pour des secondes")
        void shouldCalculate_WithPrecision_Seconds() {
            // Given - 1h23min45s à 10 kW
            double power = 10.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 11, 23, 45);

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);

            // Then
            // 1h23min45s = 5025s = 1.39583h → 10 × 1.39583 = 13.9583 kWh
            assertEquals(13.9583, consumedPower, 0.001);
        }

        @Test
        @DisplayName("Devrait maintenir la précision sur plusieurs calculs enchaînés")
        void shouldMaintain_ChainedCalculationPrecision() {
            // Given
            double power = 11.0;
            LocalDateTime startTime = LocalDateTime.of(2025, 11, 20, 10, 0);
            LocalDateTime endTime = LocalDateTime.of(2025, 11, 20, 12, 30);
            double pricePerKWh = 0.17;

            // When
            double consumedPower = powerCalculatorUtil.calculateConsumedPower(power, startTime, endTime);
            double cost = powerCalculatorUtil.calculateCost(consumedPower, pricePerKWh);

            // Then
            assertEquals(27.5, consumedPower, 0.001, "2.5h à 11 kW = 27.5 kWh");
            assertEquals(4.675, cost, 0.001, "27.5 kWh à 0.17 €/kWh = 4.675 €");
        }
    }
}

