package com.dojo.pancakelab.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a pancake with ingredients.
 * This class ensures ingredient compatibility and prevents invalid combinations.
 */
public final class Pancake {
    private final List<Ingredient> ingredients;
    private final String id;

    private Pancake(String id, List<Ingredient> ingredients) {
        this.id = id;
        this.ingredients = new ArrayList<>(ingredients);
    }

    public static Pancake create(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Pancake ID cannot be null or empty");
        }
        return new Pancake(id, new ArrayList<>());
    }

    public Pancake addIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }

        // Check compatibility with existing ingredients
        for (Ingredient existing : ingredients) {
            if (!ingredient.isCompatibleWith(existing)) {
                throw new IllegalArgumentException(
                    String.format("Ingredient '%s' is not compatible with existing ingredient '%s'", 
                                ingredient.getName(), existing.getName())
                );
            }
        }

        List<Ingredient> newIngredients = new ArrayList<>(ingredients);
        newIngredients.add(ingredient);
        return new Pancake(this.id, newIngredients);
    }

    public Pancake removeIngredient(String ingredientName) {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or empty");
        }

        List<Ingredient> newIngredients = new ArrayList<>();
        boolean found = false;
        
        for (Ingredient ingredient : ingredients) {
            if (!ingredient.getName().equalsIgnoreCase(ingredientName)) {
                newIngredients.add(ingredient);
            } else {
                found = true;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Ingredient '" + ingredientName + "' not found in pancake");
        }

        return new Pancake(this.id, newIngredients);
    }

    public String getId() {
        return id;
    }

    public List<Ingredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public boolean hasIngredient(String ingredientName) {
        return ingredients.stream()
                .anyMatch(ingredient -> ingredient.getName().equalsIgnoreCase(ingredientName));
    }

    public boolean isValid() {
        // A pancake is valid if it has at least flour and egg (basic ingredients)
        boolean hasFlour = ingredients.stream()
                .anyMatch(ingredient -> ingredient.getType() == Ingredient.IngredientType.FLOUR);
        boolean hasEgg = ingredients.stream()
                .anyMatch(ingredient -> ingredient.getType() == Ingredient.IngredientType.EGG);
        
        return hasFlour && hasEgg;
    }

    public String getDescription() {
        if (ingredients.isEmpty()) {
            return "Empty pancake";
        }
        
        StringBuilder description = new StringBuilder("Pancake with: ");
        for (int i = 0; i < ingredients.size(); i++) {
            if (i > 0) {
                description.append(", ");
            }
            description.append(ingredients.get(i).getName());
        }
        return description.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pancake pancake = (Pancake) obj;
        return Objects.equals(id, pancake.id) && Objects.equals(ingredients, pancake.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ingredients);
    }

    @Override
    public String toString() {
        return getDescription();
    }
} 