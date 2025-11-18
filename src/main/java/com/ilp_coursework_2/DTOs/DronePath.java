package com.ilp_coursework_2.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DronePath {
    @NotNull(message = "droneId not found")
    private String droneId;

    @Valid
    @NotNull(message = "deliveries not found")
    private List<Delivery> deliveryList;

    // Getters and Setters
    public String getDroneId() {return droneId;}
    public void setDroneId(String droneId) {this.droneId = droneId;}
    public List<Delivery> getDeliveryList() {return deliveryList;}
    public void setDeliveryList(List<Delivery> deliveryList) {this.deliveryList = deliveryList;}
}
