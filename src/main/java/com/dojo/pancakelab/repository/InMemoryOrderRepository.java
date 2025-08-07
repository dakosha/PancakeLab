package com.dojo.pancakelab.repository;

import com.dojo.pancakelab.domain.Order;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of OrderRepository with thread safety.
 * This implementation uses ConcurrentHashMap for thread-safe operations.
 */
public class InMemoryOrderRepository implements OrderRepository {
    
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    
    @Override
    public Order save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        orders.put(order.getId(), order);
        return order;
    }
    
    @Override
    public Optional<Order> findById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(orders.get(orderId));
    }
    
    @Override
    public List<Order> findActiveOrders() {
        return orders.values().stream()
                .filter(Order::isActive)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByStatus(Order.OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean delete(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }
        return orders.remove(orderId) != null;
    }
    
    @Override
    public boolean exists(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return false;
        }
        return orders.containsKey(orderId);
    }
    
    /**
     * Clears all orders from the repository (useful for testing).
     */
    public void clear() {
        orders.clear();
    }
    
    /**
     * Gets the total number of orders in the repository.
     * @return the number of orders
     */
    public int size() {
        return orders.size();
    }
} 