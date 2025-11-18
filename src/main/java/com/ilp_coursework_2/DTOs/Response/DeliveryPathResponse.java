package com.ilp_coursework_2.DTOs.Response;

import com.ilp_coursework_2.DTOs.DronePath;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DeliveryPathResponse {
    @NotNull(message = "Total cost not found")
    private double totalCost;
    @NotNull(message = "Total moves not found")
    private int totalMoves;
    @Valid
    @NotNull(message = "Drone paths not found")
    private List<DronePath> dronePaths;

    // Getters and Setters
    public double getTotalCost() {return totalCost;}
    public void setTotalCost(double totalCost) {this.totalCost = totalCost;}
    public int getTotalMoves() {return totalMoves;}
    public void setTotalMoves(int totalMoves) {this.totalMoves = totalMoves;}
    public List<DronePath> getDronePaths() {return dronePaths;}
    public void setDronePaths(List<DronePath> dronePaths) {this.dronePaths = dronePaths;}
}
