package com.ilp_coursework_2.DTOs.Availability;

import java.util.List;

public class ServicePoint {
    private int servicePointId;
    private List<DroneAvailability> drones;

    public int getServicePointId() { return servicePointId; }
    public void setServicePointId(int servicePointId) { this.servicePointId = servicePointId; }
    public List<DroneAvailability> getDrones() { return drones; }
    public void setDrones(List<DroneAvailability> drones) { this.drones = drones; }
}