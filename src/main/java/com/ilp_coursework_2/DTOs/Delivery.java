package com.ilp_coursework_2.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class Delivery {
    private @NotNull(message = "deliveryId not found") Integer deliveryId;
    @Valid
    @NotNull(message = "flightPath not found")
    private List<Position> flightPath;

    public Delivery() {}
    public Delivery(Integer deliveryId, List<Position> flightPath) {
        this.deliveryId = deliveryId;
        this.flightPath = flightPath;
    }

    // Getters and Setters
    public @NotNull(message = "deliveryId not found") Integer getDeliveryId() {return deliveryId;}
    public void setDeliveryId(@NotNull(message = "deliveryId not found") Integer deliveryId) {this.deliveryId = deliveryId;}
    public List<Position> getFlightPath() {return flightPath;}
    public void setFlightPath(List<Position> flightPath) {this.flightPath = flightPath;}
}
