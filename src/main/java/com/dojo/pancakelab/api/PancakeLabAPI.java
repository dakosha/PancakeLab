package com.dojo.pancakelab.api;

import com.dojo.pancakelab.api.dto.OrderDTO;
import com.dojo.pancakelab.domain.Ingredient;
import com.dojo.pancakelab.domain.Order;
import com.dojo.pancakelab.service.PancakeLabService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main API class for Pancake Lab operations.
 * This class provides the public interface and handles input validation.
 */
public class PancakeLabAPI implements PancakeLab {
    
    private final PancakeLabService service;
    
    public PancakeLabAPI(PancakeLabService service) {
        if (service == null) {
            throw new IllegalArgumentException("PancakeLabService cannot be null");
        }
        this.service = service;
    }

    /**
     * Creates a new order for the specified building and room.
     * @param building the building name (alphanumeric only)
     * @param roomNumber the room number (numeric only)
     * @return the order ID
     * @throws IllegalArgumentException if building or room number is invalid
     */
    @Override
    public String createOrder(String building, String roomNumber) {
        validateInput(building, "Building");
        validateInput(roomNumber, "Room number");
        return service.createOrder(building, roomNumber);
    }
    
    /**
     * Adds a pancake to an existing order.
     * @param orderId the order ID
     * @return the pancake ID
     * @throws IllegalArgumentException if order ID is invalid or order not found
     */
    @Override
    public String addPancakeToOrder(String orderId) {
        validateInput(orderId, "Order ID");
        return service.addPancakeToOrder(orderId);
    }
    
    /**
     * Adds an ingredient to a pancake in an order.
     * @param orderId the order ID
     * @param pancakeId the pancake ID
     * @param ingredientName the ingredient name
     * @param ingredientType the ingredient type
     * @throws IllegalArgumentException if any parameter is invalid or incompatible ingredients
     */
    @Override
    public void addIngredientToPancake(String orderId, String pancakeId, String ingredientName, String ingredientType) {
        validateInput(orderId, "Order ID");
        validateInput(pancakeId, "Pancake ID");
        validateInput(ingredientName, "Ingredient name");
        validateInput(ingredientType, "Ingredient type");
        
        Ingredient.IngredientType type = parseIngredientType(ingredientType);
        service.addIngredientToPancake(orderId, pancakeId, ingredientName, type);
    }
    
    /**
     * Removes an ingredient from a pancake in an order.
     * @param orderId the order ID
     * @param pancakeId the pancake ID
     * @param ingredientName the ingredient name to remove
     * @throws IllegalArgumentException if any parameter is invalid
     */
    @Override
    public void removeIngredientFromPancake(String orderId, String pancakeId, String ingredientName) {
        validateInput(orderId, "Order ID");
        validateInput(pancakeId, "Pancake ID");
        validateInput(ingredientName, "Ingredient name");
        service.removeIngredientFromPancake(orderId, pancakeId, ingredientName);
    }
    
    /**
     * Removes a pancake from an order.
     * @param orderId the order ID
     * @param pancakeId the pancake ID to remove
     * @throws IllegalArgumentException if any parameter is invalid
     */
    @Override
    public void removePancakeFromOrder(String orderId, String pancakeId) {
        validateInput(orderId, "Order ID");
        validateInput(pancakeId, "Pancake ID");
        service.removePancakeFromOrder(orderId, pancakeId);
    }
    
    /**
     * Completes an order.
     * @param orderId the order ID
     * @throws IllegalArgumentException if order ID is invalid or order cannot be completed
     */
    @Override
    public void completeOrder(String orderId) {
        validateInput(orderId, "Order ID");
        service.completeOrder(orderId);
    }
    
    /**
     * Cancels an order.
     * @param orderId the order ID
     * @throws IllegalArgumentException if order ID is invalid or order cannot be cancelled
     */
    @Override
    public void cancelOrder(String orderId) {
        validateInput(orderId, "Order ID");
        service.cancelOrder(orderId);
    }
    
    // Chef operations
    
    /**
     * Starts preparing an order (Chef action).
     * @param orderId the order ID
     * @throws IllegalArgumentException if order ID is invalid or order cannot be prepared
     */
    @Override
    public void startPreparingOrder(String orderId) {
        validateInput(orderId, "Order ID");
        service.startPreparingOrder(orderId);
    }
    
    /**
     * Marks an order as ready for delivery (Chef action).
     * @param orderId the order ID
     * @throws IllegalArgumentException if order ID is invalid or order cannot be marked ready
     */
    @Override
    public void markOrderReadyForDelivery(String orderId) {
        validateInput(orderId, "Order ID");
        service.markOrderReadyForDelivery(orderId);
    }
    
    // Delivery operations
    
    /**
     * Delivers an order (Delivery action).
     * @param orderId the order ID
     * @throws IllegalArgumentException if order ID is invalid or order cannot be delivered
     */
    @Override
    public void deliverOrder(String orderId) {
        validateInput(orderId, "Order ID");
        service.deliverOrder(orderId);
    }
    
    // Query operations
    
    /**
     * Gets an order by ID.
     * @param orderId the order ID
     * @return Optional containing the order DTO if found
     * @throws IllegalArgumentException if order ID is invalid
     */
    @Override
    public Optional<OrderDTO> getOrder(String orderId) {
        validateInput(orderId, "Order ID");
        return service.getOrder(orderId).map(OrderDTO::fromOrder);
    }
    
    /**
     * Gets all active orders.
     * @return list of active order DTOs
     */
    @Override
    public List<OrderDTO> getActiveOrders() {
        return service.getActiveOrders().stream()
                .map(OrderDTO::fromOrder)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets orders by status.
     * @param status the order status as string
     * @return list of order DTOs with the specified status
     * @throws IllegalArgumentException if status is invalid
     */
    @Override
    public List<OrderDTO> getOrdersByStatus(String status) {
        validateInput(status, "Status");
        Order.OrderStatus orderStatus = parseOrderStatus(status);
        return service.getOrdersByStatus(orderStatus).stream()
                .map(OrderDTO::fromOrder)
                .collect(Collectors.toList());
    }
    
    // Validation helper methods
    
    private void validateInput(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    private Ingredient.IngredientType parseIngredientType(String ingredientType) {
        try {
            return Ingredient.IngredientType.valueOf(ingredientType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ingredient type: " + ingredientType);
        }
    }
    
    private Order.OrderStatus parseOrderStatus(String status) {
        try {
            return Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }
} 