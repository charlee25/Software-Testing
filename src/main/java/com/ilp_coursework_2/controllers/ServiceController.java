package com.ilp_coursework_2.controllers;

import com.ilp_coursework_2.*;
import com.ilp_coursework_2.DTOs.Attribute;
import com.ilp_coursework_2.DTOs.Drone;
import com.ilp_coursework_2.DTOs.Position;
import com.ilp_coursework_2.DTOs.Request.AttributeRequest;
import com.ilp_coursework_2.DTOs.MedDispatchRecord;
import com.ilp_coursework_2.DTOs.Request.DistanceRequest;
import com.ilp_coursework_2.DTOs.Response.DeliveryPathResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServiceController {
    @Autowired
    private DroneService droneService;

    @GetMapping("/api/v1/dronesWithCooling/{state}")
    public List<String> getDronesWithCooling(@PathVariable boolean state) {
        return droneService.getDronesWithCooling(state);
    }
    @GetMapping("/api/v1/droneDetails/{id}")
    public Drone getDroneById(@PathVariable String id) {
        return droneService.getDroneFromID(id);
    }
    @PostMapping("/api/v1/queryAvailableDrones")
    public List<String> availableDrones(@RequestBody List<MedDispatchRecord> request) {
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
    @PostMapping("api/v1/calcDeliveryPath")
    public DeliveryPathResponse calculateDeliveryPath(@RequestBody List<MedDispatchRecord> records) {
        return droneService.calcDeliveryPath(records);
    }

    /*@PostMapping("/test")
    public List<String> testEndpoint(@RequestBody List<MedDispatchRecord> records) {
        return calcPath.getDronesForPath(records);
    }*/
}
