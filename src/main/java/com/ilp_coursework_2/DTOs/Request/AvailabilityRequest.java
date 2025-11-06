package com.ilp_coursework_2.DTOs.Request;

import com.ilp_coursework_2.DTOs.Requirements;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilityRequest {
    @NotNull(message = "ID not found")
    private int id;
    private LocalDate date;
    private LocalTime time;
    @Valid
    @NotNull(message = "Requirements not found")
    private Requirements requirements;

    //Getters and setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}
    public LocalTime getTime() {return time;}
    public void setTime(LocalTime time) {this.time = time;}
    public Requirements getRequirements() {return requirements;}
    public void setRequirements(Requirements requirements) {this.requirements = requirements;}
}
