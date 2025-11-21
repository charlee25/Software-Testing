package com.ilp_coursework_2.DTOs.Response;

import com.ilp_coursework_2.DTOs.DronePath;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DeliveryPathResponse {
    private @NotNull(message = "Total cost not found") Double totalCost;
    private @NotNull(message = "Total moves not found") Integer totalMoves;
    @Valid
    @NotNull(message = "Drone paths not found")
    private List<DronePath> dronePaths;

    // Getters and Setters
    public @NotNull(message = "Total cost not found") Double getTotalCost() {return totalCost;}
    public void setTotalCost(@NotNull(message = "Total cost not found") Double totalCost) {this.totalCost = totalCost;}
    public @NotNull(message = "Total moves not found") Integer getTotalMoves() {return totalMoves;}
    public void setTotalMoves(@NotNull(message = "Total moves not found") Integer totalMoves) {this.totalMoves = totalMoves;}
    public List<DronePath> getDronePaths() {return dronePaths;}
    public void setDronePaths(List<DronePath> dronePaths) {this.dronePaths = dronePaths;}
}
