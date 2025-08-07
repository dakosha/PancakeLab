package com.dojo.pancakelab.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Pancake domain object.
 * Tests ingredient management and validation.
 */
public class PancakeTest {
    
    private Pancake pancake;
    private Ingredient flour;
    private Ingredient egg;
    private Ingredient sugar;
    private Ingredient chocolate;
    private Ingredient mustard;
    
    @BeforeEach
    void setUp() {
        pancake = Pancake.create("pancake-1");
        flour = Ingredient.create("Flour", Ingredient.IngredientType.FLOUR);
        egg = Ingredient.create("Egg", Ingredient.IngredientType.EGG);
        sugar = Ingredient.create("Sugar", Ingredient.IngredientType.SUGAR);
        chocolate = Ingredient.create("Chocolate", Ingredient.IngredientType.SWEET_TOPPING);
        mustard = Ingredient.create("Mustard", Ingredient.IngredientType.CONDIMENT);
    }
    
    @Test
    void testCreatePancake() {
        Pancake newPancake = Pancake.create("pancake-2");
        
        assertEquals("pancake-2", newPancake.getId());
        assertTrue(newPancake.getIngredients().isEmpty());
        assertEquals("Empty pancake", newPancake.getDescription());
    }
    
    @Test
    void testCreatePancakeWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Pancake.create(null);
        });
    }
    
    @Test
    void testCreatePancakeWithEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> {
            Pancake.create("");
        });
    }
    
    @Test
    void testAddIngredient() {
        Pancake updatedPancake = pancake.addIngredient(flour);
        
        assertEquals(1, updatedPancake.getIngredients().size());
        assertTrue(updatedPancake.hasIngredient("Flour"));
        assertEquals("Pancake with: Flour", updatedPancake.getDescription());
    }
    
    @Test
    void testAddCompatibleIngredients() {
        Pancake updatedPancake = pancake
                .addIngredient(flour)
                .addIngredient(egg)
                .addIngredient(sugar);
        
        assertEquals(3, updatedPancake.getIngredients().size());
        assertTrue(updatedPancake.hasIngredient("Flour"));
        assertTrue(updatedPancake.hasIngredient("Egg"));
        assertTrue(updatedPancake.hasIngredient("Sugar"));
    }
    
    @Test
    void testAddIncompatibleIngredients() {
        Pancake pancakeWithFlour = pancake.addIngredient(flour)
                        .addIngredient(sugar);
        
        assertThrows(IllegalArgumentException.class, () -> {
            Pancake pancakeWithFlourAndMustard = pancakeWithFlour.addIngredient(mustard);
            System.out.println(pancakeWithFlourAndMustard.getDescription());
        });
    }
    
    @Test
    void testAddNullIngredient() {
        assertThrows(IllegalArgumentException.class, () -> {
            pancake.addIngredient(null);
        });
    }
    
    @Test
    void testRemoveIngredient() {
        Pancake pancakeWithIngredients = pancake
                .addIngredient(flour)
                .addIngredient(egg);
        
        Pancake updatedPancake = pancakeWithIngredients.removeIngredient("Flour");
        
        assertEquals(1, updatedPancake.getIngredients().size());
        assertFalse(updatedPancake.hasIngredient("Flour"));
        assertTrue(updatedPancake.hasIngredient("Egg"));
    }
    
    @Test
    void testRemoveNonExistentIngredient() {
        Pancake pancakeWithFlour = pancake.addIngredient(flour);
        
        assertThrows(IllegalArgumentException.class, () -> {
            pancakeWithFlour.removeIngredient("Sugar");
        });
    }
    
    @Test
    void testRemoveIngredientWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            pancake.removeIngredient(null);
        });
    }
    
    @Test
    void testRemoveIngredientWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            pancake.removeIngredient("");
        });
    }
    
    @Test
    void testPancakeValidity() {
        // Empty pancake is not valid
        assertFalse(pancake.isValid());
        
        // Pancake with only flour is not valid
        Pancake pancakeWithFlour = pancake.addIngredient(flour);
        assertFalse(pancakeWithFlour.isValid());
        
        // Pancake with flour and egg is valid
        Pancake validPancake = pancakeWithFlour.addIngredient(egg);
        assertTrue(validPancake.isValid());
        
        // Pancake with additional ingredients is still valid
        Pancake completePancake = validPancake.addIngredient(sugar);
        assertTrue(completePancake.isValid());
    }
    
    @Test
    void testHasIngredient() {
        Pancake pancakeWithFlour = pancake.addIngredient(flour);
        
        assertTrue(pancakeWithFlour.hasIngredient("Flour"));
        assertTrue(pancakeWithFlour.hasIngredient("flour")); // Case insensitive
        assertFalse(pancakeWithFlour.hasIngredient("Sugar"));
    }
    
    @Test
    void testGetDescription() {
        assertEquals("Empty pancake", pancake.getDescription());
        
        Pancake pancakeWithIngredients = pancake
                .addIngredient(flour)
                .addIngredient(egg)
                .addIngredient(sugar);
        
        assertEquals("Pancake with: Flour, Egg, Sugar", pancakeWithIngredients.getDescription());
    }
    
    @Test
    void testEqualsAndHashCode() {
        Pancake pancake1 = Pancake.create("pancake-1");
        Pancake pancake2 = Pancake.create("pancake-1");
        Pancake pancake3 = Pancake.create("pancake-2");
        
        assertEquals(pancake1, pancake2);
        assertNotEquals(pancake1, pancake3);
        assertEquals(pancake1.hashCode(), pancake2.hashCode());
        assertNotEquals(pancake1.hashCode(), pancake3.hashCode());
    }
    
    @Test
    void testImmutability() {
        Pancake originalPancake = pancake.addIngredient(flour);
        Pancake modifiedPancake = originalPancake.addIngredient(egg);
        
        // Original pancake should remain unchanged
        assertEquals(1, originalPancake.getIngredients().size());
        assertEquals(2, modifiedPancake.getIngredients().size());
    }
} 