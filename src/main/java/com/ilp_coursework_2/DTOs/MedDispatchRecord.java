package com.ilp_coursework_2.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class MedDispatchRecord {
    private @NotNull(message = "ID not found") Integer id;
    private LocalDate date;
    private LocalTime time;
    @Valid
    private Requirements requirements;
    @Valid
    @NotNull(message = "Delivery co-ordinates not found")
    private Position delivery;


    //Getters and setters
    public @NotNull(message = "ID not found") Integer getId() {return id;}
    public void setId(@NotNull(message = "ID not found") Integer id) {this.id = id;}
    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}
    public LocalTime getTime() {return time;}
    public void setTime(LocalTime time) {this.time = time;}
    public Requirements getRequirements() {return requirements;}
    public void setRequirements(Requirements requirements) {this.requirements = requirements;}
    public Position getDeliveryPosition() {return delivery;}
    public void setDeliveryPosition(Position deliveryPosition) {this.delivery = deliveryPosition;}
}
