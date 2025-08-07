package com.dojo.pancakelab;

import com.dojo.pancakelab.api.PancakeLab;
import com.dojo.pancakelab.api.PancakeLabAPI;
import com.dojo.pancakelab.api.dto.OrderDTO;
import com.dojo.pancakelab.repository.InMemoryOrderRepository;
import com.dojo.pancakelab.service.PancakeLabService;

import java.util.List;
import java.util.Optional;

/**
 * Demo class to demonstrate the Pancake Lab system functionality.
 */
public class PancakeLabDemo {
    
    public static void main(String[] args) {
        System.out.println("Pancake Lab - Dojo Ordering System\n");
        
        // Initialize the system
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        PancakeLabService service = new PancakeLabService(repository);
        PancakeLab api = new PancakeLabAPI(service);
        
        try {
            // Demo 1: Disciple creates an order
            System.out.println("1. Creating an order");
            String orderId = api.createOrder("BuildingA", "101");
            System.out.println("   Order created with ID: " + orderId);
            
            // Demo 2: Disciple adds pancakes to the order
            System.out.println("2. Disciple adds pancakes to the order");
            String pancake1Id = api.addPancakeToOrder(orderId);
            String pancake2Id = api.addPancakeToOrder(orderId);
            System.out.println("   Added pancakes: " + pancake1Id + ", " + pancake2Id);
            
            // Demo 3: Disciple adds ingredients to pancakes
            System.out.println("3. Disciple adds ingredients to pancakes");
            api.addIngredientToPancake(orderId, pancake1Id, "Flour", "FLOUR");
            api.addIngredientToPancake(orderId, pancake1Id, "Egg", "EGG");
            api.addIngredientToPancake(orderId, pancake1Id, "Sugar", "SUGAR");
            api.addIngredientToPancake(orderId, pancake1Id, "Chocolate", "SWEET_TOPPING");
            
            api.addIngredientToPancake(orderId, pancake2Id, "Flour", "FLOUR");
            api.addIngredientToPancake(orderId, pancake2Id, "Egg", "EGG");
            api.addIngredientToPancake(orderId, pancake2Id, "Salt", "SALT");
            api.addIngredientToPancake(orderId, pancake2Id, "Cheese", "SAVORY_TOPPING");
            
            System.out.println("Added ingredients to both pancakes");
            
            // Demo 4: View the order
            System.out.println("4. View the order");
            Optional<OrderDTO> order = api.getOrder(orderId);
            if (order.isPresent()) {
                OrderDTO orderDTO = order.get();
                System.out.println("   Order: " + orderDTO.getDeliveryAddress());
                System.out.println("   Status: " + orderDTO.getStatus());
                System.out.println("   Pancakes: " + orderDTO.getPancakes().size());
                orderDTO.getPancakes().forEach(pancake -> {
                    System.out.println("     - " + pancake.getDescription() + " (Valid: " + pancake.isValid() + ")");
                });
            }
            
            // Demo 5: Disciple completes the order
            System.out.println("5. Disciple completes the order");
            api.completeOrder(orderId);
            System.out.println("   Order completed successfully");
            
            // Demo 6: Chef starts preparing
            System.out.println("6. Chef starts preparing the order");
            api.startPreparingOrder(orderId);
            System.out.println("   Order is now being prepared");
            
            // Demo 7: Chef marks order ready for delivery
            System.out.println("7. Chef marks order ready for delivery");
            api.markOrderReadyForDelivery(orderId);
            System.out.println("   Order is ready for delivery");
            
            // Demo 8: Delivery delivers the order
            System.out.println("8. Delivery delivers the order");
            api.deliverOrder(orderId);
            System.out.println("   Order delivered successfully");
            
            // Demo 9: View final order status
            System.out.println("9. Final order status");
            Optional<OrderDTO> finalOrder = api.getOrder(orderId);
            if (finalOrder.isPresent()) {
                OrderDTO orderDTO = finalOrder.get();
                System.out.println("   Order: " + orderDTO.getDeliveryAddress());
                System.out.println("   Status: " + orderDTO.getStatus());
                System.out.println("   Active: " + orderDTO.isActive());
            }
            
            // Demo 10: Show validation - try to create invalid ingredient combination
            System.out.println("10. Validation Demo - Try invalid ingredient combination");
            try {
                String invalidOrderId = api.createOrder("BuildingB", "202");
                String invalidPancakeId = api.addPancakeToOrder(invalidOrderId);
                api.addIngredientToPancake(invalidOrderId, invalidPancakeId, "Flour", "FLOUR");
                api.addIngredientToPancake(invalidOrderId, invalidPancakeId, "Egg", "EGG");
                // This should fail - mustard with chocolate
                api.addIngredientToPancake(invalidOrderId, invalidPancakeId, "Mustard with Chocolate", "CONDIMENT");
            } catch (IllegalArgumentException e) {
                System.out.println("Validation working: " + e.getMessage());
            }
            
            // Demo 11: Show active orders
            System.out.println("11. Active orders");
            List<OrderDTO> activeOrders = api.getActiveOrders();
            System.out.println("Active orders: " + activeOrders.size());
            
            System.out.println("Demo completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error during demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 