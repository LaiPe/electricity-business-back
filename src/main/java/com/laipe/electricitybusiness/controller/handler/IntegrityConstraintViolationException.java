package com.laipe.electricitybusiness.controller.handler;

import lombok.Getter;

@Getter
public class IntegrityConstraintViolationException extends RuntimeException {

    private final Object inconsistentFieldName;
    private final Object inconsistentFieldValue;
    private final Class<?> ressourceEntityClass;

    public IntegrityConstraintViolationException(final Object inconsistentFieldName, final Object inconsistentFieldValue, final Class<?> ressourceEntityClass) {
        super("Data constraint violation for " +
                ressourceEntityClass.getSimpleName() + " with " +
                inconsistentFieldName + " field of value " + inconsistentFieldValue
        );
        this.inconsistentFieldName = inconsistentFieldName;
        this.inconsistentFieldValue = inconsistentFieldValue;
        this.ressourceEntityClass = ressourceEntityClass;
    }

    @Override
    public String toString() {
        return "IntegrityConstraintViolationException resulting to sending 400 response with message : '" + this.getMessage() + "'";
    }
}
