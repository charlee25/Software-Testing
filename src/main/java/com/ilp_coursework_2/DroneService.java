package com.ilp_coursework_2;

import com.ilp_coursework_2.DTOs.Availability.DroneAvailability;
import com.ilp_coursework_2.DTOs.Availability.ServicePoint;
import com.ilp_coursework_2.DTOs.Request.AttributeRequest;
import com.ilp_coursework_2.DTOs.Availability.MedDispatchRecord;
import com.ilp_coursework_2.DTOs.Request.DeliveryPathRequest;
import com.ilp_coursework_2.DTOs.Requirements;
import com.ilp_coursework_2.DTOs.Attribute;
import com.ilp_coursework_2.DTOs.Response.DeliveryPathResponse;
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
    private final RestTemplate restTemplate;
    private final String ilpEndpoint;
    @Autowired
    public DroneService(RestTemplate restTemplate, String ilpEndpoint) {
        this.restTemplate = restTemplate;
        this.ilpEndpoint = ilpEndpoint;
    }

    public List<String> getDronesWithCooling(boolean state) {
        // Fetch raw JSON as List of Maps
        String URL = ilpEndpoint + "/drones";
        List<Map<String, Object>> drones = restTemplate.getForObject(URL, List.class);

        // Filter by cooling capability and return IDs
        return drones.stream()
                .filter(d -> {
                    Map<String, Object> capability = (Map<String, Object>) d.get("capability");
                    return capability != null && Boolean.TRUE.equals(capability.get("cooling")) == state;
                })
                .map(d -> (String) d.get("id"))
                .collect(Collectors.toList());
    }
    public Map<String, Object> getDroneFromID(String ID) {
        String URL = ilpEndpoint + "/drones";
        List<Map<String, Object>> drones = restTemplate.getForObject(URL, List.class);

        return drones.stream()
                .filter(d -> {
                    Object idObj = (String) d.get("id");
                    return ID.equals(idObj);
                })
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drone not found"));
    }
    public List<String> getAvailableDronesCapability(List<MedDispatchRecord> request) {
        String URL = ilpEndpoint + "/drones";
        List<Map<String, Object>> drones = restTemplate.getForObject(URL, List.class);
        List<Boolean> matchesList = new java.util.ArrayList<>();

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

                    for(MedDispatchRecord reqRecord : request){
                        Requirements req = reqRecord.getRequirements();

                        boolean capacityMatches = droneCapacity >= req.getCapacity();

                        boolean heatingMatches = !req.getHeating() || droneHeating;
                        boolean coolingMatches = !req.getCooling() || droneCooling;

                        boolean costMatches = req.getMaxCost() == 0 || droneCost <= req.getMaxCost();

                        matchesList.add(capacityMatches && heatingMatches && coolingMatches && costMatches);
                    }
                    return checkForInvalid(matchesList);
                })
                .map(d -> (String) d.get("id"))
                .collect(Collectors.toList());
    }
    public List<String> getAvailableDronesDateTime() {

        String url = ilpEndpoint + "/drones-for-service-points";
        ServicePoint[] servicePoints = restTemplate.getForObject(url, ServicePoint[].class);

        // Collect all drones from all service points and filter by availability
        return Arrays.stream(servicePoints)
                .flatMap(sp -> sp.getDrones().stream()) //
                .filter(this::isDroneAvailableNow)
                .map(DroneAvailability::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAvailableDronesCombined(List<MedDispatchRecord> request) {
        List<String> capabilityDrones = getAvailableDronesCapability(request);
        List<String> dateTimeDrones = getAvailableDronesDateTime();

        // Keep only IDs that are in both lists
        capabilityDrones.retainAll(dateTimeDrones);

        return capabilityDrones;
    }
    public List<String> getDroneFromPath(Attribute request) {
        String url = ilpEndpoint + "/drones";
        List<Map<String, Object>> drones = restTemplate.getForObject(url, List.class);
        String attributeName = request.getAttribute();
        String attributeOperator= request.getOperator();
        String attributeValue = request.getValue();

        return drones.stream()
                .filter(drone -> {
                    return checkMatchesAttributes(drone,attributeName,attributeValue,attributeOperator);
                })
                // Map the matched drone to its ID as string
                .map(d -> d.get("id").toString())
                .collect(Collectors.toList());
    }
    public List<String> getDronesFromAttributes(AttributeRequest request){
        String url = ilpEndpoint + "/drones";
        List<Map<String, Object>> drones = restTemplate.getForObject(url, List.class);
        List<Boolean> matchesList = new java.util.ArrayList<>();

        return drones.stream()
                .filter(drone -> {
                    for (Attribute condition : request.getAttributes()) {
                        String attributeName = condition.getAttribute();
                        String operator = condition.getOperator();
                        String targetValue = condition.getValue();

                        matchesList.add(checkMatchesAttributes(drone, attributeName, targetValue, operator));
                    }
                    return checkForInvalid(matchesList);

                })
                .map(d -> d.get("id").toString())
                .collect(Collectors.toList());
    }
    /*public DeliveryPathResponse calcDeliveryPath(DeliveryPathRequest request) {
        String url = ilpEndpoint + "/calculate-delivery-path";
        List<String> dronePaths = restTemplate.postForObject(url, request, List.class);
    }*/

    //----------------------------------------Helper functions------------------------------------------------

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

    // Helper function for attribute matching
    private boolean matches(Object value, String target, String operator) {
        if (value instanceof Number) {
            try{
                double numericTarget = Double.parseDouble(target);
                double numericValue = ((Number) value).doubleValue();
                return switch (operator) {
                    case "=" -> numericValue == numericTarget;
                    case "!=" -> numericValue != numericTarget;
                    case "<" -> numericValue < numericTarget;
                    case ">" -> numericValue > numericTarget;
                    default -> false;
                };
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (value instanceof Boolean) {
            try {
                boolean boolTarget = Boolean.parseBoolean(target);
                boolean boolValue = (Boolean) value;
                return switch (operator) {
                    case "=" -> boolValue == boolTarget;
                    case "!=" -> boolValue != boolTarget;
                    default -> false;
                };
            } catch (Exception e) {
                return false;
            }
        } else if (value != null) {
            return target.equals(value.toString());
        }
        return false;
    }

    private boolean checkMatchesAttributes(Map<String, Object> drone, String attributeName, String targetValue, String operator) {
        // Check top-level attribute
        Object topLevelValue = drone.get(attributeName);
        boolean matchFound = false;

        if (topLevelValue != null) {
            matchFound = matches(topLevelValue, targetValue, operator);
        } else {
            // Check capability attribute (nested map)
            Object capabilityObj = drone.get("capability");
            if (capabilityObj instanceof Map) {
                Map<String, Object> capabilityMap = (Map<String, Object>) capabilityObj;
                Object capValue = capabilityMap.get(attributeName);
                if (capValue != null) {
                    matchFound = matches(capValue, targetValue, operator);
                }
            }
        }
        return matchFound;
    }

    private boolean checkForInvalid (List<Boolean> matchesList){
        for (Boolean match : matchesList) {
            if (!match) {
                matchesList.clear();
                return false;
            }
        }
        return true;
    }
}
