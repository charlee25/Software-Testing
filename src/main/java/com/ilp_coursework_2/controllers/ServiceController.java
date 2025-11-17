package com.ilp_coursework_2.controllers;

import com.ilp_coursework_2.*;
import com.ilp_coursework_2.DTOs.Attribute;
import com.ilp_coursework_2.DTOs.Request.AttributeRequest;
import com.ilp_coursework_2.DTOs.Availability.MedDispatchRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ServiceController {
    @Autowired
    private DroneService droneService;

    @GetMapping("/api/v1/dronesWithCooling/{state}")
    public List<String> getDronesWithCooling(@PathVariable boolean state) {
        return droneService.getDronesWithCooling(state);
    }
    @GetMapping("/api/v1/droneDetails/{id}")
    public Map<String, Object> getDroneById(@PathVariable String id) {
        return droneService.getDroneFromID(id);
    }
    @PostMapping("/api/v1/queryAvailableDrones")
    public List<String> availableDrones(@RequestBody MedDispatchRecord request) {
        return droneService.getAvailableDronesCombined(request);
    }
    @GetMapping("/api/v1/queryAsPath/{attribute_name}/{attribute_value}")
    public List<String> getDroneByPath(@PathVariable String attribute_name, @PathVariable String attribute_value) {
        Attribute attribute = new Attribute(attribute_name, "=", attribute_value);
        return droneService.getDroneFromPath(attribute);
    }
    @PostMapping("/api/v1/query")
    public List<String> getDronesByAttributes(@RequestBody List<Attribute> attributes) {
        AttributeRequest request = new AttributeRequest(attributes);
        return droneService.getDronesFromAttributes(request);
    }
}
