package com.ilp_coursework_2.controllers;

import com.ilp_coursework_2.DTOs.Request.AvailabilityRequest;
import com.ilp_coursework_2.DroneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AvailabilityController {
    @Autowired
    private DroneService droneService;
    @PostMapping("/api/v1/queryAvailableDrones")
    public List<Integer> availableDrones(@RequestBody AvailabilityRequest request) {
        return droneService.getAvailableDronesCombined(request);
    }
}
