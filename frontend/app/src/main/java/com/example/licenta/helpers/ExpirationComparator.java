package com.example.licenta.helpers;

import com.example.licenta.classes.Ingredient;

import java.util.Comparator;

public class ExpirationComparator implements Comparator<Ingredient> {
    @Override
    public int compare(Ingredient ingredient1, Ingredient ingredient2) {
        return ingredient1.getExpirationDate().compareTo(ingredient2.getExpirationDate());
    }
}
