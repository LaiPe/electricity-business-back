package com.laipe.electricitybusiness.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ModelUtil {
    public static <T> void copyFields(T source, T destination) {
        // Raise exception if given objects are null
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Given objects cannot be null");
        }

        // Raise exception if objects aren't entities
        if (!source.getClass().getPackageName().endsWith("model") || !destination.getClass().getPackageName().endsWith("model")) {
            throw new IllegalArgumentException("Given objects must come from model package");
        }

        // Raise exception if entities aren't same type
        if (!source.getClass().equals(destination.getClass())) {
            throw new IllegalArgumentException("Source and Destination entities types are not the same");
        }

        // Using reflect
        try {
            // class' fields list
            Field[] champs = source.getClass().getDeclaredFields();

            // for each field of the class
            for (Field champ : champs) {
                // if field is static or final -> ignore field
                if (Modifier.isStatic(champ.getModifiers()) ||
                        Modifier.isFinal(champ.getModifiers())) {
                    continue;
                }

                // if field is the id -> ignore field
                if (champ.getName().equals("id")) {
                    continue;
                }

                champ.setAccessible(true);
                Object valeur = champ.get(source);

                // Ignore fields of value null
                if (valeur != null || champ.getType().isPrimitive()) {
                    champ.set(destination,valeur);
                }
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while trying to copy fields by using reflecting", e);
        }
    }
}
