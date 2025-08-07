package com.dojo.pancakelab.service;

import com.dojo.pancakelab.domain.Ingredient;
import com.dojo.pancakelab.domain.Order;
import com.dojo.pancakelab.repository.InMemoryOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PancakeLabService.
 * Tests business logic and thread safety.
 */
public class PancakeLabServiceTest {
    
    private PancakeLabService service;
    private InMemoryOrderRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
        service = new PancakeLabService(repository);
    }
    
    @Test
    void testCreateOrder() {
        String orderId = service.createOrder("BuildingA", "101");
        
        assertNotNull(orderId);
        assertTrue(repository.exists(orderId));
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals("BuildingA", order.getBuilding());
        assertEquals("101", order.getRoomNumber());
        assertEquals(Order.OrderStatus.CREATED, order.getStatus());
    }
    
    @Test
    void testCreateOrderWithInvalidBuilding() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.createOrder("Building-A", "101");
        });
    }
    
    @Test
    void testCreateOrderWithInvalidRoomNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.createOrder("BuildingA", "101A");
        });
    }
    
    @Test
    void testAddPancakeToOrder() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        assertNotNull(pancakeId);
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals(1, order.getPancakes().size());
        assertEquals(pancakeId, order.getPancakes().get(0).getId());
    }
    
    @Test
    void testAddPancakeToNonExistentOrder() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addPancakeToOrder("non-existent-order");
        });
    }
    
    @Test
    void testAddIngredientToPancake() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals(1, order.getPancakes().size());
        
        var pancake = order.getPancakes().get(0);
        assertEquals(2, pancake.getIngredients().size());
        assertTrue(pancake.hasIngredient("Flour"));
        assertTrue(pancake.hasIngredient("Egg"));
    }
    
    @Test
    void testAddIncompatibleIngredients() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        service.addIngredientToPancake(orderId, pancakeId, "Sugar", Ingredient.IngredientType.SUGAR);
        
        // Try to add incompatible ingredient
        assertThrows(IllegalArgumentException.class, () -> {
            service.addIngredientToPancake(orderId, pancakeId, "Mustard", Ingredient.IngredientType.CONDIMENT);
        });
    }
    
    @Test
    void testAddIngredientToNonExistentPancake() {
        String orderId = service.createOrder("BuildingA", "101");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.addIngredientToPancake(orderId, "non-existent-pancake", "Flour", Ingredient.IngredientType.FLOUR);
        });
    }
    
    @Test
    void testRemoveIngredientFromPancake() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        
        service.removeIngredientFromPancake(orderId, pancakeId, "Flour");
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        
        var pancake = order.getPancakes().get(0);
        assertEquals(1, pancake.getIngredients().size());
        assertFalse(pancake.hasIngredient("Flour"));
        assertTrue(pancake.hasIngredient("Egg"));
    }
    
    @Test
    void testRemoveNonExistentIngredient() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.removeIngredientFromPancake(orderId, pancakeId, "Sugar");
        });
    }
    
    @Test
    void testRemovePancakeFromOrder() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancake1Id = service.addPancakeToOrder(orderId);
        String pancake2Id = service.addPancakeToOrder(orderId);
        
        service.removePancakeFromOrder(orderId, pancake1Id);
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals(1, order.getPancakes().size());
        assertEquals(pancake2Id, order.getPancakes().get(0).getId());
    }
    
    @Test
    void testCompleteOrder() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        
        service.completeOrder(orderId);
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals(Order.OrderStatus.COMPLETED, order.getStatus());
    }
    
    @Test
    void testCompleteOrderWithInvalidPancake() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        // Add only flour (missing egg)
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        
        assertThrows(IllegalStateException.class, () -> {
            service.completeOrder(orderId);
        });
    }
    
    @Test
    void testCancelOrder() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.cancelOrder(orderId);
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());
    }
    
    @Test
    void testStartPreparingOrder() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        service.completeOrder(orderId);
        
        service.startPreparingOrder(orderId);
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals(Order.OrderStatus.PREPARING, order.getStatus());
    }
    
    @Test
    void testMarkOrderReadyForDelivery() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        service.completeOrder(orderId);
        service.startPreparingOrder(orderId);
        
        service.markOrderReadyForDelivery(orderId);
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals(Order.OrderStatus.READY_FOR_DELIVERY, order.getStatus());
    }
    
    @Test
    void testDeliverOrder() {
        String orderId = service.createOrder("BuildingA", "101");
        String pancakeId = service.addPancakeToOrder(orderId);
        
        service.addIngredientToPancake(orderId, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        service.completeOrder(orderId);
        service.startPreparingOrder(orderId);
        service.markOrderReadyForDelivery(orderId);
        
        service.deliverOrder(orderId);
        
        Order order = repository.findById(orderId).orElse(null);
        assertNotNull(order);
        assertEquals(Order.OrderStatus.DELIVERED, order.getStatus());
    }
    
    @Test
    void testGetOrder() {
        String orderId = service.createOrder("BuildingA", "101");
        
        var order = service.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals(orderId, order.get().getId());
    }
    
    @Test
    void testGetNonExistentOrder() {
        var order = service.getOrder("non-existent-order");
        assertFalse(order.isPresent());
    }
    
    @Test
    void testGetActiveOrders() {
        String orderId1 = service.createOrder("BuildingA", "101");
        String orderId2 = service.createOrder("BuildingB", "202");
        
        // Complete one order
        String pancakeId = service.addPancakeToOrder(orderId1);
        service.addIngredientToPancake(orderId1, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId1, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        service.completeOrder(orderId1);
        
        var activeOrders = service.getActiveOrders();
        assertEquals(2, activeOrders.size()); // Both orders are still active
    }
    
    @Test
    void testGetOrdersByStatus() {
        String orderId1 = service.createOrder("BuildingA", "101");
        String orderId2 = service.createOrder("BuildingB", "202");
        
        // Complete one order
        String pancakeId = service.addPancakeToOrder(orderId1);
        service.addIngredientToPancake(orderId1, pancakeId, "Flour", Ingredient.IngredientType.FLOUR);
        service.addIngredientToPancake(orderId1, pancakeId, "Egg", Ingredient.IngredientType.EGG);
        service.completeOrder(orderId1);
        
        var createdOrders = service.getOrdersByStatus(Order.OrderStatus.CREATED);
        var completedOrders = service.getOrdersByStatus(Order.OrderStatus.COMPLETED);
        
        assertEquals(1, createdOrders.size());
        assertEquals(1, completedOrders.size());
    }
    
    @Test
    void testServiceWithNullRepository() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PancakeLabService(null);
        });
    }
} 