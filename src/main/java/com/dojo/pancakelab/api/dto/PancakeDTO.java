package com.dojo.pancakelab.api.dto;

import com.dojo.pancakelab.domain.Pancake;
import java.util.List;
import java.util.stream.Collectors;

public final class PancakeDTO {
    private final String id;
    private final List<String> ingredients;
    private final String description;
    private final boolean isValid;

    private PancakeDTO(String id, List<String> ingredients, String description, boolean isValid) {
        this.id = id;
        this.ingredients = ingredients;
        this.description = description;
        this.isValid = isValid;
    }

    public static PancakeDTO fromPancake(Pancake pancake) {
        List<String> ingredientNames = pancake.getIngredients().stream()
                .map(ingredient -> ingredient.getName())
                .collect(Collectors.toList());

        return new PancakeDTO(
            pancake.getId(),
            ingredientNames,
            pancake.getDescription(),
            pancake.isValid()
        );
    }

    public String getId() {
        return id;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValid() {
        return isValid;
    }

    @Override
    public String toString() {
        return String.format("PancakeDTO{id='%s', description='%s', ingredientCount=%d, valid=%s}",
                           id, description, ingredients.size(), isValid);
    }
} 