package com.dojo.pancakelab.api;

import com.dojo.pancakelab.api.dto.OrderDTO;

import java.util.List;
import java.util.Optional;

public interface PancakeLab {
    String createOrder(String building, String roomNumber);

    String addPancakeToOrder(String orderId);

    void addIngredientToPancake(String orderId, String pancakeId, String ingredientName, String ingredientType);

    void removeIngredientFromPancake(String orderId, String pancakeId, String ingredientName);

    void removePancakeFromOrder(String orderId, String pancakeId);

    void completeOrder(String orderId);

    void cancelOrder(String orderId);

    void startPreparingOrder(String orderId);

    void markOrderReadyForDelivery(String orderId);

    void deliverOrder(String orderId);

    Optional<OrderDTO> getOrder(String orderId);

    List<OrderDTO> getActiveOrders();

    List<OrderDTO> getOrdersByStatus(String status);
}
