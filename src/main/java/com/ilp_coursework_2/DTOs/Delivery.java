package com.ilp_coursework_2.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class Delivery {
    private @NotNull(message = "deliveryId not found") Integer deliveryId;
    @Valid
    @NotNull(message = "positions not found")
    private List<Position> positions;

    // Getters and Setters
    public @NotNull(message = "deliveryId not found") Integer getDeliveryId() {return deliveryId;}
    public void setDeliveryId(@NotNull(message = "deliveryId not found") Integer deliveryId) {this.deliveryId = deliveryId;}
    public List<Position> getPositions() {return positions;}
    public void setPositions(List<Position> positions) {this.positions = positions;}
}
