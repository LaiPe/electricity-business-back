package com.laipe.electricitybusiness.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.chrono.ChronoLocalDateTime;

/**
 * Validateur pour l'annotation {@link StartDateBeforeEndDate}.
 * Vérifie que la date de début précède la date de fin.
 */
public class StartDateBeforeEndDateValidator implements ConstraintValidator<StartDateBeforeEndDate, Object> {

    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(StartDateBeforeEndDate constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDateField();
        this.endDateField = constraintAnnotation.endDateField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            ChronoLocalDateTime<?> startDate = getFieldValue(value, startDateField);
            ChronoLocalDateTime<?> endDate = getFieldValue(value, endDateField);

            // Si l'une des dates est nulle, on laisse les autres validations (@NotNull) gérer
            if (startDate == null || endDate == null) {
                return true;
            }

            if (!startDate.isBefore(endDate)) {
                // Désactiver le message par défaut et créer un message personnalisé
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(startDateField)
                        .addConstraintViolation();
                return false;
            }

            return true;
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Erreur lors de la validation des dates")
                    .addConstraintViolation();
            return false;
        }
    }

    private ChronoLocalDateTime<?> getFieldValue(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object fieldValue = field.get(object);

        if (fieldValue == null) {
            return null;
        }

        if (fieldValue instanceof ChronoLocalDateTime<?>) {
            return (ChronoLocalDateTime<?>) fieldValue;
        }

        throw new IllegalArgumentException("Le champ " + fieldName + " doit être de type ChronoLocalDateTime");
    }
}

