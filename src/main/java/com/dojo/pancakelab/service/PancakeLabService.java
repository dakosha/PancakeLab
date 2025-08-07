package com.dojo.pancakelab.service;

import com.dojo.pancakelab.domain.Ingredient;
import com.dojo.pancakelab.domain.Order;
import com.dojo.pancakelab.domain.Pancake;
import com.dojo.pancakelab.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main service class for Pancake Lab operations.
 * This class provides the business logic and ensures thread safety.
 */
public class PancakeLabService {
    
    private final OrderRepository orderRepository;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public PancakeLabService(OrderRepository orderRepository) {
        if (orderRepository == null) {
            throw new IllegalArgumentException("OrderRepository cannot be null");
        }
        this.orderRepository = orderRepository;
    }
    
    /**
     * Creates a new order for the specified building and room.
     * @param building the building name
     * @param roomNumber the room number
     * @return the order ID
     */
    public String createOrder(String building, String roomNumber) {
        lock.writeLock().lock();
        try {
            Order order = Order.create(building, roomNumber);
            orderRepository.save(order);
            return order.getId();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Adds a pancake to an existing order.
     * @param orderId the order ID
     * @return the pancake ID
     */
    public String addPancakeToOrder(String orderId) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Pancake pancake = Pancake.create(UUID.randomUUID().toString());
            Order updatedOrder = order.addPancake(pancake);
            orderRepository.save(updatedOrder);
            return pancake.getId();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Adds an ingredient to a pancake in an order.
     * @param orderId the order ID
     * @param pancakeId the pancake ID
     * @param ingredientName the ingredient name
     * @param ingredientType the ingredient type
     */
    public void addIngredientToPancake(String orderId, String pancakeId, String ingredientName, Ingredient.IngredientType ingredientType) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Pancake pancake = findPancakeInOrder(order, pancakeId);
            
            Ingredient ingredient = Ingredient.create(ingredientName, ingredientType);
            Pancake updatedPancake = pancake.addIngredient(ingredient);
            
            Order updatedOrder = replacePancakeInOrder(order, pancakeId, updatedPancake);
            orderRepository.save(updatedOrder);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Removes an ingredient from a pancake in an order.
     * @param orderId the order ID
     * @param pancakeId the pancake ID
     * @param ingredientName the ingredient name to remove
     */
    public void removeIngredientFromPancake(String orderId, String pancakeId, String ingredientName) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Pancake pancake = findPancakeInOrder(order, pancakeId);
            
            Pancake updatedPancake = pancake.removeIngredient(ingredientName);
            Order updatedOrder = replacePancakeInOrder(order, pancakeId, updatedPancake);
            orderRepository.save(updatedOrder);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Removes a pancake from an order.
     * @param orderId the order ID
     * @param pancakeId the pancake ID to remove
     */
    public void removePancakeFromOrder(String orderId, String pancakeId) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Order updatedOrder = order.removePancake(pancakeId);
            orderRepository.save(updatedOrder);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Completes an order.
     * @param orderId the order ID
     */
    public void completeOrder(String orderId) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Order completedOrder = order.complete();
            orderRepository.save(completedOrder);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Cancels an order.
     * @param orderId the order ID
     */
    public void cancelOrder(String orderId) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Order cancelledOrder = order.cancel();
            orderRepository.save(cancelledOrder);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Starts preparing an order (Chef action).
     * @param orderId the order ID
     */
    public void startPreparingOrder(String orderId) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Order preparingOrder = order.startPreparing();
            orderRepository.save(preparingOrder);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Marks an order as ready for delivery (Chef action).
     * @param orderId the order ID
     */
    public void markOrderReadyForDelivery(String orderId) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Order readyOrder = order.readyForDelivery();
            orderRepository.save(readyOrder);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Delivers an order (Delivery action).
     * @param orderId the order ID
     */
    public void deliverOrder(String orderId) {
        lock.writeLock().lock();
        try {
            Order order = getOrderOrThrow(orderId);
            Order deliveredOrder = order.deliver();
            orderRepository.save(deliveredOrder);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Gets an order by ID.
     * @param orderId the order ID
     * @return Optional containing the order if found
     */
    public Optional<Order> getOrder(String orderId) {
        lock.readLock().lock();
        try {
            return orderRepository.findById(orderId);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets all active orders.
     * @return list of active orders
     */
    public List<Order> getActiveOrders() {
        lock.readLock().lock();
        try {
            return orderRepository.findActiveOrders();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Gets orders by status.
     * @param status the order status
     * @return list of orders with the specified status
     */
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        lock.readLock().lock();
        try {
            return orderRepository.findByStatus(status);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    private Order getOrderOrThrow(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }
    
    private Pancake findPancakeInOrder(Order order, String pancakeId) {
        return order.getPancakes().stream()
                .filter(pancake -> pancake.getId().equals(pancakeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pancake not found: " + pancakeId));
    }
    
    private Order replacePancakeInOrder(Order order, String pancakeId, Pancake updatedPancake) {
        // Remove the old pancake and add the updated one
        Order orderWithoutPancake = order.removePancake(pancakeId);
        return orderWithoutPancake.addPancake(updatedPancake);
    }
} 