package com.laipe.electricitybusiness.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class GeolocatorUtil {

    /**
     * Calcule la distance en kilomètres entre deux points géographiques en utilisant la formule de Haversine.
     * La formule de Haversine est utilisée pour calculer la distance entre deux points sur une sphère
     * en tenant compte de la courbure de la Terre.
     *
     * @param lat1 Latitude du premier point en degrés décimaux (-90 à 90)
     * @param lon1 Longitude du premier point en degrés décimaux (-180 à 180)
     * @param lat2 Latitude du deuxième point en degrés décimaux (-90 à 90)
     * @param lon2 Longitude du deuxième point en degrés décimaux (-180 à 180)
     * @return La distance en kilomètres entre les deux points
     * @throws IllegalArgumentException si l'une des coordonnées est null ou en dehors des limites valides
     *                                  (latitude : -90 à 90, longitude : -180 à 180)
     */
    public double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new IllegalArgumentException("Les coordonnées ne peuvent pas être null");
        }
        if (lat1.doubleValue() < -90 || lat1.doubleValue() > 90 ||
                lat2.doubleValue() < -90 || lat2.doubleValue() > 90) {
            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90");
        }
        if (lon1.doubleValue() < -180 || lon1.doubleValue() > 180 ||
                lon2.doubleValue() < -180 || lon2.doubleValue() > 180) {
            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180");
        }

        final int R = 6371; // Rayon de la Terre en km

        double latDistance = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double lonDistance = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}

