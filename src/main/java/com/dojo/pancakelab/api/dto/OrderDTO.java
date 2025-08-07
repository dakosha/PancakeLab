package com.dojo.pancakelab.api.dto;

import com.dojo.pancakelab.domain.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public final class OrderDTO {
    private final String id;
    private final String building;
    private final String roomNumber;
    private final String deliveryAddress;
    private final List<PancakeDTO> pancakes;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean isActive;
    private final boolean canBeModified;

    private OrderDTO(String id, String building, String roomNumber, String deliveryAddress,
                    List<PancakeDTO> pancakes, String status, LocalDateTime createdAt,
                    LocalDateTime updatedAt, boolean isActive, boolean canBeModified) {
        this.id = id;
        this.building = building;
        this.roomNumber = roomNumber;
        this.deliveryAddress = deliveryAddress;
        this.pancakes = pancakes;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
        this.canBeModified = canBeModified;
    }

    public static OrderDTO fromOrder(Order order) {
        List<PancakeDTO> pancakeDTOs = order.getPancakes().stream()
                .map(PancakeDTO::fromPancake)
                .collect(Collectors.toList());

        return new OrderDTO(
            order.getId(),
            order.getBuilding(),
            order.getRoomNumber(),
            order.getDeliveryAddress(),
            pancakeDTOs,
            order.getStatus().name(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            order.isActive(),
            order.canBeModified()
        );
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

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<PancakeDTO> getPancakes() {
        return pancakes;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean canBeModified() {
        return canBeModified;
    }

    @Override
    public String toString() {
        return String.format("OrderDTO{id='%s', deliveryAddress='%s', status='%s', pancakeCount=%d}",
                           id, deliveryAddress, status, pancakes.size());
    }
} 