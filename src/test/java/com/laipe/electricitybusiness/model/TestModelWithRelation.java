package com.laipe.electricitybusiness.model;

/**
 * Modèle factice avec relation pour les tests unitaires
 */
public class TestModelWithRelation {
    private Long id;
    private String code;
    private Double price;
    private TestModel relatedModel;

    public TestModelWithRelation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public TestModel getRelatedModel() {
        return relatedModel;
    }

    public void setRelatedModel(TestModel relatedModel) {
        this.relatedModel = relatedModel;
    }
}

