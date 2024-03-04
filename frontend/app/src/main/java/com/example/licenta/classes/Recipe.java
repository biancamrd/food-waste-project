/* Assignment: 3
Campus: Ashdod
Authors:
Eliran Naduyev 312089105
Maria Garber
*/

package com.example.licenta.classes;
import androidx.annotation.NonNull;

public class Recipe {

    private String id;
    private Long recipeId;
    private String Title;
    private String Thumbnail;
    private int amountOfDishes;
    private int readyInMins;
    private String image;
    private boolean isVegetarian;
    private boolean isHealthy;
    private User userId;
    private String instructions;



    public Recipe(String id, String title, String thumbnail, int amountOfDishes, int readyInMins) {
        this.id = id;
        Title = title;
        Thumbnail = thumbnail;
        this.amountOfDishes = amountOfDishes;
        this.readyInMins = readyInMins;
    }



    public Recipe(String id, String title) {
        this.id = id;
        Title = title;
    }

    public Recipe() {

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public int getAmountOfDishes() {
        return amountOfDishes;
    }

    public int getReadyInMins() {
        return readyInMins;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public void setAmountOfDishes(int amountOfDishes) {
        this.amountOfDishes = amountOfDishes;
    }

    public void setReadyInMins(int readyInMins) {
        this.readyInMins = readyInMins;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public boolean isHealthy() {
        return isHealthy;
    }

    public void setHealthy(boolean healthy) {
        isHealthy = healthy;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    @NonNull
    @Override
    public String toString() {
        return getTitle();
    }
}
