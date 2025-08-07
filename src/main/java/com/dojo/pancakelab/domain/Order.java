package com.dojo.pancakelab.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an order for pancakes with delivery information.
 * This class manages the order lifecycle and validates delivery details.
 */
public final class Order {
    private final String id;
    private final String building;
    private final String roomNumber;
    private final List<Pancake> pancakes;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public enum OrderStatus {
        CREATED, COMPLETED, CANCELLED, PREPARING, READY_FOR_DELIVERY, DELIVERED
    }

    private Order(String id, String building, String roomNumber, List<Pancake> pancakes, 
                  OrderStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.building = building;
        this.roomNumber = roomNumber;
        this.pancakes = new ArrayList<>(pancakes);
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(String building, String roomNumber) {
        validateDeliveryInfo(building, roomNumber);
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        return new Order(id, building, roomNumber, new ArrayList<>(), OrderStatus.CREATED, now, now);
    }

    private static void validateDeliveryInfo(String building, String roomNumber) {
        if (building == null || building.trim().isEmpty()) {
            throw new IllegalArgumentException("Building cannot be null or empty");
        }
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be null or empty");
        }
        
        // Validate building format (should be alphanumeric)
        if (!building.matches("^[A-Za-z0-9]+$")) {
            throw new IllegalArgumentException("Building must contain only alphanumeric characters");
        }
        
        // Validate room number format (should be numeric)
        if (!roomNumber.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("Room number must contain only numeric characters");
        }
    }

    public Order addPancake(Pancake pancake) {
        if (pancake == null) {
            throw new IllegalArgumentException("Pancake cannot be null");
        }
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add pancakes to order in status: " + status);
        }

        List<Pancake> newPancakes = new ArrayList<>(pancakes);
        newPancakes.add(pancake);
        return new Order(id, building, roomNumber, newPancakes, status, createdAt, LocalDateTime.now());
    }

    public Order removePancake(String pancakeId) {
        if (pancakeId == null || pancakeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pancake ID cannot be null or empty");
        }
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot remove pancakes from order in status: " + status);
        }

        List<Pancake> newPancakes = new ArrayList<>();
        boolean found = false;
        
        for (Pancake pancake : pancakes) {
            if (!pancake.getId().equals(pancakeId)) {
                newPancakes.add(pancake);
            } else {
                found = true;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Pancake with ID '" + pancakeId + "' not found in order");
        }

        return new Order(id, building, roomNumber, newPancakes, status, createdAt, LocalDateTime.now());
    }

    public Order complete() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot complete order in status: " + status);
        }
        if (pancakes.isEmpty()) {
            throw new IllegalStateException("Cannot complete order with no pancakes");
        }
        
        // Validate all pancakes
        for (Pancake pancake : pancakes) {
            if (!pancake.isValid()) {
                throw new IllegalStateException("Pancake " + pancake.getId() + " is not valid");
            }
        }

        return new Order(id, building, roomNumber, pancakes, OrderStatus.COMPLETED, createdAt, LocalDateTime.now());
    }

    public Order cancel() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot cancel order in status: " + status);
        }
        return new Order(id, building, roomNumber, pancakes, OrderStatus.CANCELLED, createdAt, LocalDateTime.now());
    }

    public Order startPreparing() {
        if (status != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot start preparing order in status: " + status);
        }
        return new Order(id, building, roomNumber, pancakes, OrderStatus.PREPARING, createdAt, LocalDateTime.now());
    }

    public Order readyForDelivery() {
        if (status != OrderStatus.PREPARING) {
            throw new IllegalStateException("Cannot mark order ready for delivery in status: " + status);
        }
        return new Order(id, building, roomNumber, pancakes, OrderStatus.READY_FOR_DELIVERY, createdAt, LocalDateTime.now());
    }

    public Order deliver() {
        if (status != OrderStatus.READY_FOR_DELIVERY) {
            throw new IllegalStateException("Cannot deliver order in status: " + status);
        }
        return new Order(id, building, roomNumber, pancakes, OrderStatus.DELIVERED, createdAt, LocalDateTime.now());
    }

    public String getId() {
        return id;
    }

    public String getBuilding() {
        return building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public List<Pancake> getPancakes() {
        return Collections.unmodifiableList(pancakes);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return status == OrderStatus.CREATED || status == OrderStatus.COMPLETED || 
               status == OrderStatus.PREPARING || status == OrderStatus.READY_FOR_DELIVERY;
    }

    public boolean canBeModified() {
        return status == OrderStatus.CREATED;
    }

    public String getDeliveryAddress() {
        return building + " - Room " + roomNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order order = (Order) obj;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Order %s - %s (%s) - %d pancakes", 
                           id, getDeliveryAddress(), status, pancakes.size());
    }
} 