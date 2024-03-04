package com.example.licenta.classes;

public class RecipeIngredient {
    private Long id;
    private Recipe recipe;
    private String name;
    private String unit;
    private float quantity;

    public RecipeIngredient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "id=" + id +
                ", recipe=" + recipe +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
