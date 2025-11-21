package com.ilp_coursework_2.DTOs;

import jakarta.validation.constraints.NotNull;

public class Requirements {
    private @NotNull(message = "Capacity not found") Double capacity;
    private Boolean cooling = false;
    private Boolean heating = false;
    private Double maxCost = Double.MAX_VALUE;


    // Getters and Setters
    public @NotNull(message = "Capacity not found") Double getCapacity() {return capacity;}
    public void setCapacity(@NotNull(message = "Capacity not found") Double capacity) {this.capacity = capacity;}
    public Boolean getCooling() {return cooling;}
    public void setCooling(Boolean cooling) {this.cooling = cooling;}
    public Boolean getHeating() {return heating;}
    public void setHeating(Boolean heating) {this.heating = heating;}
    public Double getMaxCost() {return maxCost;}
    public void setMaxCost(Double maxCost) {this.maxCost = maxCost;}

}
