package com.ilp_coursework_2;

import com.ilp_coursework_2.DTOs.Response.DronesWithCoolingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DroneService {

    private static final String ILP_ENDPOINT = "https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net";

    @Autowired
    private RestTemplate restTemplate;

    public List<Integer> getDronesWithCooling(boolean state) {
        // Fetch raw JSON as List of Maps
        String URL = ILP_ENDPOINT + "/drones";
        List<Map<String, Object>> drones = restTemplate.getForObject(URL, List.class);

        // Filter by cooling capability and return IDs
        return drones.stream()
                .filter(d -> {
                    Map<String, Object> capability = (Map<String, Object>) d.get("capability");
                    return capability != null && Boolean.TRUE.equals(capability.get("cooling")) == state;
                })
                .map(d -> (Integer) d.get("id"))
                .collect(Collectors.toList());
    }
    public Map<String, Object> getDroneFromID(Integer ID) {
        String URL = ILP_ENDPOINT + "/drones";
        List<Map<String, Object>> drones = restTemplate.getForObject(URL, List.class);

        return drones.stream()
                .filter(d -> {
                    Object idObj = d.get("id");
                    if (idObj instanceof Integer) {
                        return ID.equals((Integer) idObj);
                    } else if (idObj instanceof Number) { // sometimes JSON maps numbers to Long
                        return ID.equals(((Number) idObj).intValue());
                    } else {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drone not found"));
    }
}
