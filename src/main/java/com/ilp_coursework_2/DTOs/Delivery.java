package com.ilp_coursework_2.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class Delivery {
    @NotNull(message = "deliveryId not found")
    private int deliveryId;
    @Valid
    @NotNull(message = "positions not found")
    private List<Position> positions;

    // Getters and Setters
    public int getDeliveryId() {return deliveryId;}
    public void setDeliveryId(int deliveryId) {this.deliveryId = deliveryId;}
    public List<Position> getPositions() {return positions;}
    public void setPositions(List<Position> positions) {this.positions = positions;}
}
