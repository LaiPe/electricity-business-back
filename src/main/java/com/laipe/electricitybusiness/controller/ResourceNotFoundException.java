package com.laipe.electricitybusiness.controller;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

  private final Object inconsistentId;
  private final Class<?> ressourceEntityClass;

  public ResourceNotFoundException(final Object inconsistentId, final Class<?> ressourceEntityClass) {
    super(ressourceEntityClass.getSimpleName() + " not found with id " + inconsistentId);
    this.inconsistentId = inconsistentId;
    this.ressourceEntityClass = ressourceEntityClass;
  }

  @Override
  public String toString() {
    return "ResourceNotFoundException resulting to sending 404 response with message : \"" + this.getMessage() + "\"";
  }
}