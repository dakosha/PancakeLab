package com.dojo.pancakelab.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Order domain object.
 * Tests order lifecycle and validation.
 */
public class OrderTest {
    
    private Order order;
    private Pancake pancake1;
    private Pancake pancake2;
    
    @BeforeEach
    void setUp() {
        order = Order.create("BuildingA", "101");
        pancake1 = Pancake.create("pancake-1")
                .addIngredient(Ingredient.create("Flour", Ingredient.IngredientType.FLOUR))
                .addIngredient(Ingredient.create("Egg", Ingredient.IngredientType.EGG));
        pancake2 = Pancake.create("pancake-2")
                .addIngredient(Ingredient.create("Flour", Ingredient.IngredientType.FLOUR))
                .addIngredient(Ingredient.create("Egg", Ingredient.IngredientType.EGG))
                .addIngredient(Ingredient.create("Sugar", Ingredient.IngredientType.SUGAR));
    }
    
    @Test
    void testCreateOrder() {
        Order newOrder = Order.create("BuildingB", "202");
        
        assertNotNull(newOrder.getId());
        assertEquals("BuildingB", newOrder.getBuilding());
        assertEquals("202", newOrder.getRoomNumber());
        assertEquals("BuildingB - Room 202", newOrder.getDeliveryAddress());
        assertEquals(Order.OrderStatus.CREATED, newOrder.getStatus());
        assertTrue(newOrder.isActive());
        assertTrue(newOrder.canBeModified());
        assertTrue(newOrder.getPancakes().isEmpty());
    }
    
    @Test
    void testCreateOrderWithNullBuilding() {
        assertThrows(IllegalArgumentException.class, () -> {
            Order.create(null, "101");
        });
    }
    
    @Test
    void testCreateOrderWithEmptyBuilding() {
        assertThrows(IllegalArgumentException.class, () -> {
            Order.create("", "101");
        });
    }
    
    @Test
    void testCreateOrderWithNullRoomNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            Order.create("BuildingA", null);
        });
    }
    
    @Test
    void testCreateOrderWithEmptyRoomNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            Order.create("BuildingA", "");
        });
    }
    
    @Test
    void testCreateOrderWithInvalidBuildingFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            Order.create("Building-A", "101");
        });
    }
    
    @Test
    void testCreateOrderWithInvalidRoomNumberFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            Order.create("BuildingA", "101A");
        });
    }
    
    @Test
    void testAddPancake() {
        Order updatedOrder = order.addPancake(pancake1);
        
        assertEquals(1, updatedOrder.getPancakes().size());
        assertEquals(pancake1, updatedOrder.getPancakes().get(0));
        assertEquals(Order.OrderStatus.CREATED, updatedOrder.getStatus());
    }
    
    @Test
    void testAddNullPancake() {
        assertThrows(IllegalArgumentException.class, () -> {
            order.addPancake(null);
        });
    }
    
    @Test
    void testAddPancakeToCompletedOrder() {
        Order completedOrder = order.addPancake(pancake1).complete();
        
        assertThrows(IllegalStateException.class, () -> {
            completedOrder.addPancake(pancake2);
        });
    }
    
    @Test
    void testRemovePancake() {
        Order orderWithPancakes = order.addPancake(pancake1).addPancake(pancake2);
        
        Order updatedOrder = orderWithPancakes.removePancake(pancake1.getId());
        
        assertEquals(1, updatedOrder.getPancakes().size());
        assertEquals(pancake2, updatedOrder.getPancakes().get(0));
    }
    
    @Test
    void testRemoveNonExistentPancake() {
        Order orderWithPancake = order.addPancake(pancake1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            orderWithPancake.removePancake("non-existent-id");
        });
    }
    
    @Test
    void testRemovePancakeWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            order.removePancake(null);
        });
    }
    
    @Test
    void testRemovePancakeWithEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> {
            order.removePancake("");
        });
    }
    
    @Test
    void testRemovePancakeFromCompletedOrder() {
        Order completedOrder = order.addPancake(pancake1).complete();
        
        assertThrows(IllegalStateException.class, () -> {
            completedOrder.removePancake(pancake1.getId());
        });
    }
    
    @Test
    void testCompleteOrder() {
        Order orderWithPancake = order.addPancake(pancake1);
        
        Order completedOrder = orderWithPancake.complete();
        
        assertEquals(Order.OrderStatus.COMPLETED, completedOrder.getStatus());
        assertTrue(completedOrder.isActive());
        assertFalse(completedOrder.canBeModified());
    }
    
    @Test
    void testCompleteEmptyOrder() {
        assertThrows(IllegalStateException.class, () -> {
            order.complete();
        });
    }
    
    @Test
    void testCompleteOrderWithInvalidPancake() {
        Pancake invalidPancake = Pancake.create("invalid-pancake");
        Order orderWithInvalidPancake = order.addPancake(invalidPancake);
        
        assertThrows(IllegalStateException.class, () -> {
            orderWithInvalidPancake.complete();
        });
    }
    
    @Test
    void testCompleteAlreadyCompletedOrder() {
        Order completedOrder = order.addPancake(pancake1).complete();
        
        assertThrows(IllegalStateException.class, () -> {
            completedOrder.complete();
        });
    }
    
    @Test
    void testCancelOrder() {
        Order orderWithPancake = order.addPancake(pancake1);
        
        Order cancelledOrder = orderWithPancake.cancel();
        
        assertEquals(Order.OrderStatus.CANCELLED, cancelledOrder.getStatus());
        assertFalse(cancelledOrder.isActive());
        assertFalse(cancelledOrder.canBeModified());
    }
    
    @Test
    void testCancelAlreadyCompletedOrder() {
        Order completedOrder = order.addPancake(pancake1).complete();
        
        assertThrows(IllegalStateException.class, () -> {
            completedOrder.cancel();
        });
    }
    
    @Test
    void testStartPreparingOrder() {
        Order completedOrder = order.addPancake(pancake1).complete();
        
        Order preparingOrder = completedOrder.startPreparing();
        
        assertEquals(Order.OrderStatus.PREPARING, preparingOrder.getStatus());
        assertTrue(preparingOrder.isActive());
        assertFalse(preparingOrder.canBeModified());
    }
    
    @Test
    void testStartPreparingNonCompletedOrder() {
        Order orderWithPancake = order.addPancake(pancake1);
        
        assertThrows(IllegalStateException.class, () -> {
            orderWithPancake.startPreparing();
        });
    }
    
    @Test
    void testReadyForDelivery() {
        Order preparingOrder = order.addPancake(pancake1).complete().startPreparing();
        
        Order readyOrder = preparingOrder.readyForDelivery();
        
        assertEquals(Order.OrderStatus.READY_FOR_DELIVERY, readyOrder.getStatus());
        assertTrue(readyOrder.isActive());
        assertFalse(readyOrder.canBeModified());
    }
    
    @Test
    void testReadyForDeliveryFromNonPreparingOrder() {
        Order completedOrder = order.addPancake(pancake1).complete();
        
        assertThrows(IllegalStateException.class, () -> {
            completedOrder.readyForDelivery();
        });
    }
    
    @Test
    void testDeliverOrder() {
        Order readyOrder = order.addPancake(pancake1).complete().startPreparing().readyForDelivery();
        
        Order deliveredOrder = readyOrder.deliver();
        
        assertEquals(Order.OrderStatus.DELIVERED, deliveredOrder.getStatus());
        assertFalse(deliveredOrder.isActive());
        assertFalse(deliveredOrder.canBeModified());
    }
    
    @Test
    void testDeliverNonReadyOrder() {
        Order preparingOrder = order.addPancake(pancake1).complete().startPreparing();
        
        assertThrows(IllegalStateException.class, () -> {
            preparingOrder.deliver();
        });
    }
    
    @Test
    void testOrderImmutability() {
        Order originalOrder = order.addPancake(pancake1);
        Order modifiedOrder = originalOrder.addPancake(pancake2);
        
        // Original order should remain unchanged
        assertEquals(1, originalOrder.getPancakes().size());
        assertEquals(2, modifiedOrder.getPancakes().size());
    }
    
    @Test
    void testEqualsAndHashCode() {
        Order order1 = Order.create("BuildingA", "101");
        Order order2 = Order.create("BuildingA", "101");
        Order order3 = Order.create("BuildingB", "101");
        
        // Orders with same ID should be equal
        assertEquals(order1, order1);
        assertNotEquals(order1, order2); // Different IDs
        assertNotEquals(order1, order3);
        assertNotEquals(order1.hashCode(), order2.hashCode());
    }
    
    @Test
    void testToString() {
        Order orderWithPancake = order.addPancake(pancake1);
        String orderString = orderWithPancake.toString();
        
        assertTrue(orderString.contains("Order"));
        assertTrue(orderString.contains("BuildingA - Room 101"));
        assertTrue(orderString.contains("CREATED"));
        assertTrue(orderString.contains("1 pancakes"));
    }
} 