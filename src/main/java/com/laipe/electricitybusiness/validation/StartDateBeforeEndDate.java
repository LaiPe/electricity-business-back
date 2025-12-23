package com.laipe.electricitybusiness.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation de validation pour s'assurer que la date de début précède la date de fin.
 * À utiliser au niveau de la classe.
 */
@Documented
@Constraint(validatedBy = StartDateBeforeEndDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartDateBeforeEndDate {

    String message() default "La date de début doit précéder la date de fin prévue";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Nom du champ représentant la date de début.
     */
    String startDateField() default "startDate";

    /**
     * Nom du champ représentant la date de fin.
     */
    String endDateField() default "endDate";
}

