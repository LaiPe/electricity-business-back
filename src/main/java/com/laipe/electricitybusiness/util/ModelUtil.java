package com.laipe.electricitybusiness.util;

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

    public static <T,DTO> DTO toDTO(T entity) {
        // Raise exception if given object is null
        if (entity == null) {
            throw new IllegalArgumentException("Given object cannot be null");
        }

        // Raise exception if given object isn't an entity
        Class<?> entityClass = entity.getClass();
        String entityPackage = entityClass.getPackageName();
        if (!entityPackage.endsWith("model")) {
            throw new IllegalArgumentException("Given objects must come from model package");
        }

        // Create a string with the given object simple class name + "DTO"
        String dtoClassName = entityPackage.replace("model", "dto") +
                '.' + entity.getClass().getSimpleName() + "DTO";


        try {
            // Raise exception if there isn't a class with this name in the dto package
            @SuppressWarnings("unchecked")
            Class<DTO> dtoClass = (Class<DTO>) Class.forName(dtoClassName);

            // Using reflect, get entity fields list
            Field[] entityFields = entityClass.getDeclaredFields();

            // new dto
            DTO dto = dtoClass.getDeclaredConstructor().newInstance();

            // for each field of the entity
            for (Field entityField : entityFields) {
                entityField.setAccessible(true);

                try {
                    // if there is in the dto class a field with the same name
                    Field dtoField = dtoClass.getDeclaredField(entityField.getName());
                    dtoField.setAccessible(true);

                    // Copy entity field value into dto field
                    Object fieldValue = entityField.get(entity);
                    dtoField.set(dto, fieldValue);

                } catch (NoSuchFieldException e) {
                    // Field doesn't exist in DTO, skip it
                }
            }
            return dto;

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("DTO class not found: " + dtoClassName, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create DTO instance", e);
        }
    }

    public static <DTO, T> T toEntity(DTO dto) {
        // Raise exception if given object is null
        if (dto == null) {
            throw new IllegalArgumentException("Given object cannot be null");
        }

        // Raise exception if given object isn't a DTO
        Class<?> dtoClass = dto.getClass();
        String dtoPackage = dtoClass.getPackageName();
        if (!dtoPackage.endsWith("dto")) {
            throw new IllegalArgumentException("Given objects must come from dto package");
        }

        // Create a string with the corresponding entity class name
        // Remove "DTO" suffix from class name and replace "dto" with "model" in package
        String dtoSimpleName = dto.getClass().getSimpleName();
        if (!dtoSimpleName.endsWith("DTO")) {
            throw new IllegalArgumentException("DTO class name must end with 'DTO'");
        }

        String entityClassName = dtoPackage.replace("dto", "model") +
                '.' + dtoSimpleName.substring(0, dtoSimpleName.length() - 3);

        try {
            // Raise exception if there isn't a class with this name in the model package
            @SuppressWarnings("unchecked")
            Class<T> entityClass = (Class<T>) Class.forName(entityClassName);

            // Using reflection, get DTO fields list
            Field[] dtoFields = dtoClass.getDeclaredFields();

            // Create new entity instance
            T entity = entityClass.getDeclaredConstructor().newInstance();

            // For each field of the DTO
            for (Field dtoField : dtoFields) {
                dtoField.setAccessible(true);

                try {
                    // If there is in the entity class a field with the same name
                    Field entityField = entityClass.getDeclaredField(dtoField.getName());
                    entityField.setAccessible(true);

                    // Copy DTO field value into entity field
                    Object fieldValue = dtoField.get(dto);
                    entityField.set(entity, fieldValue);

                } catch (NoSuchFieldException e) {
                    // Field doesn't exist in Entity, skip it
                }
            }
            return entity;

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Entity class not found: " + entityClassName, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Entity instance", e);
        }
    }
}
