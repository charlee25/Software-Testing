package com.ilp_coursework_2.DTOs.Availability;

import java.util.List;

public class DroneAvailability {
    private String id;
    private List<AvailableWindow> availability;

    //Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<AvailableWindow> getAvailability() { return availability; }
    public void setAvailability(List<AvailableWindow> availability) { this.availability = availability; }
}
