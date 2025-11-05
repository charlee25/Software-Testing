package com.ilp_coursework_2.controllers;

import com.ilp_coursework_2.*;
import com.ilp_coursework_2.DTOs.Response.DronesWithCoolingResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class StaticController {
    @Autowired
    private DroneService droneService;

    @GetMapping("/api/v1/dronesWithCooling/{state}")
    public List<Integer> getDronesWithCooling(@PathVariable boolean state) {
        return droneService.getDronesWithCooling(state);
    }
    @GetMapping("/api/v1/droneDetails/{id}")
    public Map<String, Object> getDroneById(@PathVariable Integer id) {
        return droneService.getDroneFromID(id);
    }
}
