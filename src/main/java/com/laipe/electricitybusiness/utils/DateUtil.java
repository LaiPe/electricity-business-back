package com.laipe.electricitybusiness.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class DateUtil {

    /**
     * Vérifie si deux intervalles de temps se chevauchent.
     *
     * @param start1 Date de début du premier intervalle
     * @param end1   Date de fin du premier intervalle
     * @param start2 Date de début du deuxième intervalle
     * @param end2   Date de fin du deuxième intervalle
     * @return true si les intervalles se chevauchent, false sinon
     */
    public boolean doOverlap(LocalDateTime start1, LocalDateTime end1,
                                    LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
