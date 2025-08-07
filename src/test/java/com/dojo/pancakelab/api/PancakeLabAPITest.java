package com.dojo.pancakelab.api;

import com.dojo.pancakelab.api.dto.OrderDTO;
import com.dojo.pancakelab.repository.InMemoryOrderRepository;
import com.dojo.pancakelab.service.PancakeLabService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PancakeLabAPI.
 * Tests API validation and DTO conversion.
 */
public class PancakeLabAPITest {
    
    private PancakeLab api;
    private InMemoryOrderRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();
        PancakeLabService service = new PancakeLabService(repository);
        api = new PancakeLabAPI(service);
    }
    
    @Test
    void testCreateOrder() {
        String orderId = api.createOrder("BuildingA", "101");
        
        assertNotNull(orderId);
        assertTrue(repository.exists(orderId));
    }
    
    @Test
    void testCreateOrderWithNullBuilding() {
        assertThrows(IllegalArgumentException.class, () -> {
            api.createOrder(null, "101");
        });
    }
    
    @Test
    void testCreateOrderWithEmptyBuilding() {
        assertThrows(IllegalArgumentException.class, () -> {
            api.createOrder("", "101");
        });
    }
    
    @Test
    void testCreateOrderWithNullRoomNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            api.createOrder("BuildingA", null);
        });
    }
    
    @Test
    void testCreateOrderWithEmptyRoomNumber() {
        assertThrows(IllegalArgumentException.class, () -> {
            api.createOrder("BuildingA", "");
        });
    }
    
    @Test
    void testAddPancakeToOrder() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        assertNotNull(pancakeId);
    }
    
    @Test
    void testAddPancakeToOrderWithNullOrderId() {
        assertThrows(IllegalArgumentException.class, () -> {
            api.addPancakeToOrder(null);
        });
    }
    
    @Test
    void testAddPancakeToOrderWithEmptyOrderId() {
        assertThrows(IllegalArgumentException.class, () -> {
            api.addPancakeToOrder("");
        });
    }
    
    @Test
    void testAddIngredientToPancake() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        api.addIngredientToPancake(orderId, pancakeId, "Flour", "FLOUR");
        api.addIngredientToPancake(orderId, pancakeId, "Egg", "EGG");
        
        // Verify through DTO
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals(1, order.get().getPancakes().size());
        assertEquals(2, order.get().getPancakes().get(0).getIngredients().size());
    }
    
    @Test
    void testAddIngredientToPancakeWithNullParameters() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        assertThrows(IllegalArgumentException.class, () -> {
            api.addIngredientToPancake(null, pancakeId, "Flour", "FLOUR");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            api.addIngredientToPancake(orderId, null, "Flour", "FLOUR");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            api.addIngredientToPancake(orderId, pancakeId, null, "FLOUR");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            api.addIngredientToPancake(orderId, pancakeId, "Flour", null);
        });
    }
    
    @Test
    void testAddIngredientToPancakeWithInvalidType() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        assertThrows(IllegalArgumentException.class, () -> {
            api.addIngredientToPancake(orderId, pancakeId, "Flour", "INVALID_TYPE");
        });
    }
    
    @Test
    void testRemoveIngredientFromPancake() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        api.addIngredientToPancake(orderId, pancakeId, "Flour", "FLOUR");
        api.addIngredientToPancake(orderId, pancakeId, "Egg", "EGG");
        
        api.removeIngredientFromPancake(orderId, pancakeId, "Flour");
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals(1, order.get().getPancakes().get(0).getIngredients().size());
        assertFalse(order.get().getPancakes().get(0).getIngredients().contains("Flour"));
    }
    
    @Test
    void testRemovePancakeFromOrder() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancake1Id = api.addPancakeToOrder(orderId);
        String pancake2Id = api.addPancakeToOrder(orderId);
        
        api.removePancakeFromOrder(orderId, pancake1Id);
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals(1, order.get().getPancakes().size());
    }
    
    @Test
    void testCompleteOrder() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        api.addIngredientToPancake(orderId, pancakeId, "Flour", "FLOUR");
        api.addIngredientToPancake(orderId, pancakeId, "Egg", "EGG");
        
        api.completeOrder(orderId);
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals("COMPLETED", order.get().getStatus());
    }
    
    @Test
    void testCancelOrder() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        api.cancelOrder(orderId);
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals("CANCELLED", order.get().getStatus());
    }
    
    @Test
    void testStartPreparingOrder() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        api.addIngredientToPancake(orderId, pancakeId, "Flour", "FLOUR");
        api.addIngredientToPancake(orderId, pancakeId, "Egg", "EGG");
        api.completeOrder(orderId);
        
        api.startPreparingOrder(orderId);
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals("PREPARING", order.get().getStatus());
    }
    
    @Test
    void testMarkOrderReadyForDelivery() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        api.addIngredientToPancake(orderId, pancakeId, "Flour", "FLOUR");
        api.addIngredientToPancake(orderId, pancakeId, "Egg", "EGG");
        api.completeOrder(orderId);
        api.startPreparingOrder(orderId);
        
        api.markOrderReadyForDelivery(orderId);
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals("READY_FOR_DELIVERY", order.get().getStatus());
    }
    
    @Test
    void testDeliverOrder() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        api.addIngredientToPancake(orderId, pancakeId, "Flour", "FLOUR");
        api.addIngredientToPancake(orderId, pancakeId, "Egg", "EGG");
        api.completeOrder(orderId);
        api.startPreparingOrder(orderId);
        api.markOrderReadyForDelivery(orderId);
        
        api.deliverOrder(orderId);
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals("DELIVERED", order.get().getStatus());
    }
    
    @Test
    void testGetOrder() {
        String orderId = api.createOrder("BuildingA", "101");
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        assertEquals(orderId, order.get().getId());
        assertEquals("BuildingA", order.get().getBuilding());
        assertEquals("101", order.get().getRoomNumber());
        assertEquals("BuildingA - Room 101", order.get().getDeliveryAddress());
        assertEquals("CREATED", order.get().getStatus());
        assertTrue(order.get().isActive());
        assertTrue(order.get().canBeModified());
    }
    
    @Test
    void testGetNonExistentOrder() {
        var order = api.getOrder("non-existent-order");
        assertFalse(order.isPresent());
    }
    
    @Test
    void testGetActiveOrders() {
        String orderId1 = api.createOrder("BuildingA", "101");
        String orderId2 = api.createOrder("BuildingB", "202");
        
        var activeOrders = api.getActiveOrders();
        assertEquals(2, activeOrders.size());
    }
    
    @Test
    void testGetOrdersByStatus() {
        String orderId1 = api.createOrder("BuildingA", "101");
        String orderId2 = api.createOrder("BuildingB", "202");
        
        // Complete one order
        String pancakeId = api.addPancakeToOrder(orderId1);
        api.addIngredientToPancake(orderId1, pancakeId, "Flour", "FLOUR");
        api.addIngredientToPancake(orderId1, pancakeId, "Egg", "EGG");
        api.completeOrder(orderId1);
        
        var createdOrders = api.getOrdersByStatus("CREATED");
        var completedOrders = api.getOrdersByStatus("COMPLETED");
        
        assertEquals(1, createdOrders.size());
        assertEquals(1, completedOrders.size());
    }
    
    @Test
    void testGetOrdersByInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            api.getOrdersByStatus("INVALID_STATUS");
        });
    }
    
    @Test
    void testAPIWithNullService() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PancakeLabAPI(null);
        });
    }
    
    @Test
    void testDTOConversion() {
        String orderId = api.createOrder("BuildingA", "101");
        String pancakeId = api.addPancakeToOrder(orderId);
        
        api.addIngredientToPancake(orderId, pancakeId, "Flour", "FLOUR");
        api.addIngredientToPancake(orderId, pancakeId, "Egg", "EGG");
        
        var order = api.getOrder(orderId);
        assertTrue(order.isPresent());
        
        OrderDTO orderDTO = order.get();
        assertEquals(orderId, orderDTO.getId());
        assertEquals("BuildingA", orderDTO.getBuilding());
        assertEquals("101", orderDTO.getRoomNumber());
        assertEquals("BuildingA - Room 101", orderDTO.getDeliveryAddress());
        assertEquals("CREATED", orderDTO.getStatus());
        assertTrue(orderDTO.isActive());
        assertTrue(orderDTO.canBeModified());
        assertEquals(1, orderDTO.getPancakes().size());
        
        var pancakeDTO = orderDTO.getPancakes().get(0);
        assertEquals(pancakeId, pancakeDTO.getId());
        assertEquals(2, pancakeDTO.getIngredients().size());
        assertTrue(pancakeDTO.getIngredients().contains("Flour"));
        assertTrue(pancakeDTO.getIngredients().contains("Egg"));
        assertTrue(pancakeDTO.isValid());
    }
} 