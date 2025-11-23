package com.ilp_coursework_2.controllers;

import com.ilp_coursework_2.CW1_Calculations;
import com.ilp_coursework_2.DTOs.*;
import com.ilp_coursework_2.DTOs.Request.DistanceRequest;
import com.ilp_coursework_2.DTOs.Request.NextPositionRequest;
import com.ilp_coursework_2.DTOs.Request.RegionRequest;
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
public class CalculationController {
    CW1_Calculations calculations = new CW1_Calculations();

    //2) UID
    @GetMapping("/api/v1/uid")
    public String getUid() {return "s2479712";}

    //3) distanceTo
    @PostMapping("/api/v1/distanceTo")
    public ResponseEntity<Double> getDistance(@Valid @RequestBody DistanceRequest request) {
        return ResponseEntity.ok(calculations.getDistance(request));
    }

    //4) isCloseTo
    @PostMapping("/api/v1/isCloseTo")
    public ResponseEntity<Boolean> isCloseTo(@Valid @RequestBody DistanceRequest request){
        return ResponseEntity.ok(calculations.isCloseTo(request));
    }

    //5) nextPosition
    @PostMapping("/api/v1/nextPosition")
    public ResponseEntity<?> nextPosition(@Valid @RequestBody NextPositionRequest request){

        if (calculations.nextPosition(request).getLat() != null && calculations.nextPosition(request).getLng() != null) {
            return ResponseEntity.ok(calculations.nextPosition(request));
        }
        else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Angle must be a multiple of 22.5 degrees and between 0-360 degrees");
            return ResponseEntity.badRequest().body(error);
        }
    }

    //6) isInRegion
    @PostMapping("/api/v1/isInRegion")
    public ResponseEntity<?> isInRegion(@Valid @RequestBody RegionRequest request) {
        return ResponseEntity.ok(calculations.isInRegion(request));
    }
}
