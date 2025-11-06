package com.ilp_coursework_2.DTOs;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class Requirements {
    @NotNull(message = "Capacity not found")
    private double capacity;
    private boolean cooling;
    private boolean heating;
    private double maxCost;

    public Requirements (){

    }
    public Requirements(int capacity) {
        this.capacity = capacity;
        this.heating = false;
        this.cooling = false;
        this.maxCost = 0.0;
    }

    // Getters and Setters
    public double getCapacity() {return capacity;}
    public void setCapacity(double capacity) {this.capacity = capacity;}
    public boolean getCooling() {return cooling;}
    public void setCooling(boolean cooling) {this.cooling = cooling;}
    public boolean getHeating() {return heating;}
    public void setHeating(boolean heating) {this.heating = heating;}
    public double getMaxCost() {return maxCost;}
    public void setMaxCost(double maxCost) {this.maxCost = maxCost;}

}
