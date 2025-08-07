package com.dojo.pancakelab.repository;

import com.dojo.pancakelab.domain.Order;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Order persistence.
 * This interface abstracts the data access layer.
 */
public interface OrderRepository {
    
    /**
     * Saves an order to the repository.
     * @param order the order to save
     * @return the saved order
     */
    Order save(Order order);
    
    /**
     * Finds an order by its ID.
     * @param orderId the order ID
     * @return Optional containing the order if found
     */
    Optional<Order> findById(String orderId);
    
    /**
     * Finds all active orders.
     * @return list of active orders
     */
    List<Order> findActiveOrders();
    
    /**
     * Finds orders by status.
     * @param status the order status to filter by
     * @return list of orders with the specified status
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * Deletes an order from the repository.
     * @param orderId the order ID to delete
     * @return true if the order was deleted, false if not found
     */
    boolean delete(String orderId);
    
    /**
     * Checks if an order exists.
     * @param orderId the order ID to check
     * @return true if the order exists, false otherwise
     */
    boolean exists(String orderId);
} 