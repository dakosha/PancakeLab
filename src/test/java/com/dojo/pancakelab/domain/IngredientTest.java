package com.dojo.pancakelab.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Ingredient domain object.
 * Tests validation rules and ingredient compatibility.
 */
public class IngredientTest {
    
    private Ingredient flour;
    private Ingredient egg;
    private Ingredient sugar;
    private Ingredient chocolate;
    private Ingredient mustard;
    private Ingredient salt;
    
    @BeforeEach
    void setUp() {
        flour = Ingredient.create("Flour", Ingredient.IngredientType.FLOUR);
        egg = Ingredient.create("Egg", Ingredient.IngredientType.EGG);
        sugar = Ingredient.create("Sugar", Ingredient.IngredientType.SUGAR);
        chocolate = Ingredient.create("Chocolate", Ingredient.IngredientType.SWEET_TOPPING);
        mustard = Ingredient.create("Mustard", Ingredient.IngredientType.CONDIMENT);
        salt = Ingredient.create("Salt", Ingredient.IngredientType.SALT);
    }
    
    @Test
    void testCreateValidIngredient() {
        Ingredient ingredient = Ingredient.create("Vanilla", Ingredient.IngredientType.SWEET_TOPPING);
        
        assertEquals("Vanilla", ingredient.getName());
        assertEquals(Ingredient.IngredientType.SWEET_TOPPING, ingredient.getType());
        assertTrue(ingredient.isSweet());
        assertFalse(ingredient.isSavory());
    }
    
    @Test
    void testCreateIngredientWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ingredient.create(null, Ingredient.IngredientType.FLOUR);
        });
    }
    
    @Test
    void testCreateIngredientWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ingredient.create("", Ingredient.IngredientType.FLOUR);
        });
    }
    
    @Test
    void testCreateIngredientWithNullType() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ingredient.create("Flour", null);
        });
    }
    
    @Test
    void testCreateInvalidMustardChocolateCombination() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ingredient.create("Mustard with Chocolate", Ingredient.IngredientType.CONDIMENT);
        });
    }
    
    @Test
    void testCreateInvalidMustardMilkCombination() {
        assertThrows(IllegalArgumentException.class, () -> {
            Ingredient.create("Mustard with Milk", Ingredient.IngredientType.CONDIMENT);
        });
    }
    
    @Test
    void testSweetIngredientDetection() {
        assertTrue(sugar.isSweet());
        assertTrue(chocolate.isSweet());
        assertFalse(sugar.isSavory());
        assertFalse(chocolate.isSavory());
    }
    
    @Test
    void testSavoryIngredientDetection() {
        assertTrue(mustard.isSavory());
        assertTrue(salt.isSavory());
        assertFalse(mustard.isSweet());
        assertFalse(salt.isSweet());
    }
    
    @Test
    void testNeutralIngredientDetection() {
        assertFalse(flour.isSweet());
        assertFalse(flour.isSavory());
        assertFalse(egg.isSweet());
        assertFalse(egg.isSavory());
    }
    
    @Test
    void testCompatibleIngredients() {
        assertTrue(flour.isCompatibleWith(egg));
        assertTrue(sugar.isCompatibleWith(chocolate));
        assertTrue(salt.isCompatibleWith(mustard));
    }
    
    @Test
    void testIncompatibleSweetAndSavory() {
        assertFalse(sugar.isCompatibleWith(mustard));
        assertFalse(chocolate.isCompatibleWith(salt));
        assertFalse(mustard.isCompatibleWith(sugar));
    }
    
    @Test
    void testIncompatibleMustardAndChocolate() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            Ingredient mustardChocolate = Ingredient.create("Mustard Chocolate", Ingredient.IngredientType.CONDIMENT);
        });
        assertEquals("Mustard cannot be combined with chocolate or milk-based ingredients", ex.getMessage());
    }
    
    @Test
    void testEqualsAndHashCode() {
        Ingredient ingredient1 = Ingredient.create("Flour", Ingredient.IngredientType.FLOUR);
        Ingredient ingredient2 = Ingredient.create("Flour", Ingredient.IngredientType.FLOUR);
        Ingredient ingredient3 = Ingredient.create("Sugar", Ingredient.IngredientType.SUGAR);
        
        assertEquals(ingredient1, ingredient2);
        assertNotEquals(ingredient1, ingredient3);
        assertEquals(ingredient1.hashCode(), ingredient2.hashCode());
        assertNotEquals(ingredient1.hashCode(), ingredient3.hashCode());
    }
    
    @Test
    void testToString() {
        assertEquals("Flour", flour.toString());
        assertEquals("Chocolate", chocolate.toString());
    }
} 