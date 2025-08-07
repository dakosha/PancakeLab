package com.dojo.pancakelab.domain;

import java.util.Objects;

/**
 * Represents an ingredient that can be added to a pancake.
 * This class enforces validation rules to prevent invalid combinations.
 */
public final class Ingredient {
    private final String name;
    private final IngredientType type;
    private final boolean isSweet;
    private final boolean isSavory;

    public enum IngredientType {
        FLOUR, EGG, MILK, SUGAR, SALT, BUTTER, 
        SWEET_TOPPING, SAVORY_TOPPING, SPICE, CONDIMENT
    }

    private Ingredient(String name, IngredientType type, boolean isSweet, boolean isSavory) {
        this.name = name;
        this.type = type;
        this.isSweet = isSweet;
        this.isSavory = isSavory;
    }

    public static Ingredient create(String name, IngredientType type) {
        validateIngredient(name, type);
        boolean isSweet = isSweetIngredient(name, type);
        boolean isSavory = isSavoryIngredient(name, type);
        return new Ingredient(name, type, isSweet, isSavory);
    }

    private static void validateIngredient(String name, IngredientType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Ingredient type cannot be null");
        }
        
        // Prevent known invalid ingredients
        String lowerName = name.toLowerCase();
        if (lowerName.contains("mustard") && (lowerName.contains("chocolate") || lowerName.contains("milk"))) {
            throw new IllegalArgumentException("Mustard cannot be combined with chocolate or milk-based ingredients");
        }
    }

    private static boolean isSweetIngredient(String name, IngredientType type) {
        String lowerName = name.toLowerCase();
        return type == IngredientType.SWEET_TOPPING || 
               lowerName.contains("sugar") || 
               lowerName.contains("chocolate") || 
               lowerName.contains("honey") || 
               lowerName.contains("syrup") ||
               lowerName.contains("cream");
    }

    private static boolean isSavoryIngredient(String name, IngredientType type) {
        String lowerName = name.toLowerCase();
        return type == IngredientType.SAVORY_TOPPING || 
               type == IngredientType.CONDIMENT ||
               lowerName.contains("salt") || 
               lowerName.contains("pepper") || 
               lowerName.contains("mustard") ||
               lowerName.contains("cheese");
    }

    public String getName() {
        return name;
    }

    public IngredientType getType() {
        return type;
    }

    public boolean isSweet() {
        return isSweet;
    }

    public boolean isSavory() {
        return isSavory;
    }

    public boolean isCompatibleWith(Ingredient other) {
        // Sweet and savory ingredients are generally incompatible
        if (this.isSweet && other.isSavory) {
            return false;
        }
        if (this.isSavory && other.isSweet) {
            return false;
        }
        
        // Specific incompatible combinations
        String thisName = this.name.toLowerCase();
        String otherName = other.name.toLowerCase();
        
        if ((thisName.contains("mustard") && otherName.contains("chocolate")) ||
            (thisName.contains("chocolate") && otherName.contains("mustard"))) {
            return false;
        }
        
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ingredient that = (Ingredient) obj;
        return Objects.equals(name, that.name) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return name;
    }
}
