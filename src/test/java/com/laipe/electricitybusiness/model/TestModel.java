package com.laipe.electricitybusiness.model;

/**
 * Modèle factice simple pour les tests unitaires
 */
public class TestModel {
    private Long id;
    private String name;
    private String description;
    private Integer quantity;

    public TestModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

