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

    /**
     * Trouve toutes les bornes dans un rayon donné autour d'un point géographique
     * @param longitude Longitude du point central
     * @param latitude Latitude du point central
     * @param rayon Rayon de recherche en kilomètres
     * @return Liste des bornes trouvées dans le rayon spécifié
     */
//    public List<BorneDTO> get_nearby_borne(BigDecimal longitude, BigDecimal latitude, double rayon) {
//        if (longitude == null || latitude == null) {
//            throw new IllegalArgumentException("Les coordonnées ne peuvent pas être null");
//        }
//        if (longitude.doubleValue() < -180 || longitude.doubleValue() > 180) {
//            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180");
//        }
//        if (latitude.doubleValue() < -90 || latitude.doubleValue() > 90) {
//            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90");
//        }
//
//        return borneService.findAll().stream()
//                .filter(borne -> calculateDistance(latitude, longitude, borne.getLatitude(), borne.getLongitude()) <= rayon)
//                .map(entityMapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Trouve toutes les bornes libres à un moment donné
//     * @param time Moment pour lequel on cherche les bornes libres
//     * @return Liste des bornes libres au moment spécifié
//     */
//    public List<BorneDTO> get_free_borne(LocalDateTime time) {
//        if (time == null) {
//            throw new IllegalArgumentException("Le temps ne peut pas être null");
//        }
//
//        List<Borne> allBornes = borneService.findAll();
//        List<Reservation> activeReservations = reservationService.findAll().stream()
//                .filter(r -> r.getEtat() == EtatReservation.ACCEPTEE)
//                .filter(r -> time.isAfter(r.getDateDebut()) && time.isBefore(r.getDateFin()))
//                .collect(Collectors.toList());
//
//        return allBornes.stream()
//                .filter(borne -> activeReservations.stream()
//                        .noneMatch(reservation -> reservation.getBorne().getNumBorne().equals(borne.getNumBorne())))
//                .map(entityMapper::toDTO)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Trouve toutes les bornes libres dans un rayon donné autour d'un point géographique à un moment donné.
//     * Cette méthode combine les résultats de get_free_borne et get_nearby_borne pour obtenir les bornes
//     * qui sont à la fois libres et dans le rayon spécifié.
//     *
//     * @param longitude Longitude du point central en degrés décimaux (-180 à 180)
//     * @param latitude Latitude du point central en degrés décimaux (-90 à 90)
//     * @param rayon Rayon de recherche en kilomètres
//     * @param time Moment pour lequel on cherche les bornes libres
//     * @return Liste des bornes libres trouvées dans le rayon spécifié au moment donné
//     * @throws IllegalArgumentException si le temps est null ou si les coordonnées sont invalides
//     */
//    public List<BorneDTO> get_free_nearby_borne(BigDecimal longitude, BigDecimal latitude, double rayon, LocalDateTime time) {
//        if (time == null) {
//            throw new IllegalArgumentException("Le temps ne peut pas être null");
//        }
//        if (longitude == null || latitude == null) {
//            throw new IllegalArgumentException("Les coordonnées ne peuvent pas être null");
//        }
//        if (longitude.doubleValue() < -180 || longitude.doubleValue() > 180) {
//            throw new IllegalArgumentException("La longitude doit être comprise entre -180 et 180");
//        }
//        if (latitude.doubleValue() < -90 || latitude.doubleValue() > 90) {
//            throw new IllegalArgumentException("La latitude doit être comprise entre -90 et 90");
//        }
//
//        // Obtenir les bornes libres au moment donné
//        List<BorneDTO> freeBornes = get_free_borne(time);
//
//        // Obtenir les bornes dans le rayon spécifié
//        List<BorneDTO> nearbyBornes = get_nearby_borne(longitude, latitude, rayon);
//
//        // Retourner l'intersection des deux listes
//        return freeBornes.stream()
//                .filter(freeBorne -> nearbyBornes.stream()
//                        .anyMatch(nearbyBorne -> nearbyBorne.getNumBorne().equals(freeBorne.getNumBorne())))
//                .collect(Collectors.toList());
//    }
}

