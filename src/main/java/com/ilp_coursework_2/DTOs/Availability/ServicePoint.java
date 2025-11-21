package com.ilp_coursework_2.DTOs.Availability;

import java.util.List;

public class ServicePoint {
    private Integer servicePointId;
    private List<DroneAvailability> drones;

    public Integer getServicePointId() { return servicePointId; }
    public void setServicePointId(Integer servicePointId) { this.servicePointId = servicePointId; }
    public List<DroneAvailability> getDrones() { return drones; }
    public void setDrones(List<DroneAvailability> drones) { this.drones = drones; }
}