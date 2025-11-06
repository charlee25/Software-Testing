package com.ilp_coursework_2;

import com.ilp_coursework_2.DTOs.Availability.DroneAvailability;
import com.ilp_coursework_2.DTOs.Availability.ServicePoint;
import com.ilp_coursework_2.DTOs.Request.AvailabilityRequest;
import com.ilp_coursework_2.DTOs.Requirements;
import com.ilp_coursework_2.DTOs.Response.DronesWithCoolingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
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

    public List<Integer> getAvailableDronesCapability(AvailabilityRequest request) {
        String URL = ILP_ENDPOINT + "/drones";
        List<Map<String, Object>> drones = restTemplate.getForObject(URL, List.class);

        return drones.stream()
                .filter(d -> {
                    Map<String, Object> capability = (Map<String, Object>) d.get("capability");
                    if (capability == null) {
                        return false;
                    }
                    boolean droneCooling = (boolean) capability.get("cooling");
                    boolean droneHeating = (boolean) capability.get("heating");
                    double droneCapacity = ((Number) capability.get("capacity")).doubleValue();

                    double droneCost = ((Number) capability.get("costInitial")).doubleValue()
                            + ((Number) capability.get("costFinal")).doubleValue()
                            + (((Number) capability.get("costPerMove")).doubleValue()
                            * ((Number) capability.get("maxMoves")).doubleValue());

                    Requirements req = request.getRequirements();

                    boolean capacityMatches = droneCapacity >= req.getCapacity();

                    boolean heatingMatches = !req.getHeating() || droneHeating;
                    boolean coolingMatches = !req.getCooling() || droneCooling;

                    boolean costMatches = req.getMaxCost() == 0 || droneCost <= req.getMaxCost();

                    return capacityMatches && heatingMatches && coolingMatches && costMatches;
                })
                .map(d -> (Integer) d.get("id"))
                .collect(Collectors.toList());
    }
    public List<Integer> getAvailableDronesDateTime(AvailabilityRequest request) {
        int servicePointId = request.getId();

        String url = ILP_ENDPOINT + "/drones-for-service-points";
        ServicePoint[] servicePoints = restTemplate.getForObject(url, ServicePoint[].class);

        // Find the requested service point
        ServicePoint sp = Arrays.stream(servicePoints)
                .filter(p -> p.getServicePointId() == servicePointId)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service point not found"));

        // Return list of IDs for available drones
        return sp.getDrones().stream()
                .filter(this::isDroneAvailableNow)
                .map(d -> Integer.parseInt(d.getId())) // convert String ID → Integer
                .collect(Collectors.toList());
    }
    public List<Integer> getAvailableDronesCombined(AvailabilityRequest request) {
        List<Integer> capabilityDrones = getAvailableDronesCapability(request);
        List<Integer> dateTimeDrones = getAvailableDronesDateTime(request);

        // Keep only IDs that are in both lists
        capabilityDrones.retainAll(dateTimeDrones);

        return capabilityDrones;
    }

    //Helper function for date time availability
    private boolean isDroneAvailableNow(DroneAvailability drone) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        LocalTime now = LocalTime.now();

        return drone.getAvailability().stream().anyMatch(window -> {
            DayOfWeek windowDay = DayOfWeek.valueOf(window.getDayOfWeek());
            LocalTime from = LocalTime.parse(window.getFrom());
            LocalTime until = LocalTime.parse(window.getUntil());

            return windowDay == today &&
                    (now.equals(from) || now.isAfter(from)) &&
                    (now.equals(until) || now.isBefore(until));
        });
    }
}
