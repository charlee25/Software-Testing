package com.ilp_coursework_2;

import com.ilp_coursework_2.DTOs.*;
import com.ilp_coursework_2.DTOs.Availability.AvailableWindow;
import com.ilp_coursework_2.DTOs.Availability.DroneAvailability;
import com.ilp_coursework_2.DTOs.Availability.ServicePoint;
import com.ilp_coursework_2.DTOs.Request.AttributeRequest;
import com.ilp_coursework_2.DTOs.MedDispatchRecord;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DroneService {
    private final RestTemplate restTemplate;
    private final String ilpEndpoint;

    public DroneService(RestTemplate restTemplate, String ilpEndpoint) {
        this.restTemplate = restTemplate;
        this.ilpEndpoint = ilpEndpoint;
    }

    public List<Drone> getAllDrones() {
        String URL = ilpEndpoint + "/drones";
        Drone[] drones = restTemplate.getForObject(URL, Drone[].class);
        return Arrays.asList(drones);
    }

    public List<String> getDronesWithCooling(boolean state) {
        List<Drone> drones = getAllDrones();

        // Filter by cooling capability and return IDs
        return drones.stream()
                .filter(d -> d.getCapability() != null && d.getCapability().getCooling() == state)
                .map(d -> (String) d.getId())
                .collect(Collectors.toList());
    }

    public Drone getDroneFromID(String ID) {
        List<Drone> drones = getAllDrones();

        return drones.stream()
                .filter(d -> {
                    return d.getId().equals(ID);
                })
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Drone not found"));
    }

    public List<String> getAvailableDronesCapability(List<MedDispatchRecord> records) {
        List<Drone> drones = getAllDrones();

        return drones.stream()
                .filter(drone -> {

                    Capability capability = drone.getCapability();
                    System.out.println(capability);
                    if (capability == null) {
                        return false;
                    }

                    double droneCapacity = capability.getCapacity();
                    boolean droneCooling = capability.getCooling();
                    boolean droneHeating = capability.getHeating();
                    double droneCost = calculateMaxCost(drone);

                    // Drone is valid only if it matches ALL records
                    return records.stream().allMatch(record -> {

                        Requirements req = record.getRequirements();

                        boolean capacityMatches = droneCapacity >= req.getCapacity();
                        boolean heatingMatches = !req.getHeating() || droneHeating;
                        boolean coolingMatches = !req.getCooling() || droneCooling;
                        boolean costMatches = req.getMaxCost() != null || droneCost <= req.getMaxCost();

                        System.out.printf("Drone %s - Capacity: %b, Heating: %b, Cooling: %b, Cost: %b%n",
                                drone.getId(),
                                capacityMatches,
                                heatingMatches,
                                coolingMatches,
                                costMatches
                        );
                        return capacityMatches && heatingMatches && coolingMatches && costMatches;
                    });
                })
                .map(Drone::getId)
                .collect(Collectors.toList());
    }

    public List<String> getAvailableDronesDateTime(List<MedDispatchRecord> records) {
        // Load all drones from all service points
        String url = ilpEndpoint + "/drones-for-service-points";
        ServicePoint[] servicePoints = restTemplate.getForObject(url, ServicePoint[].class);

        // Flatten all drones across all service points
        List<DroneAvailability> allDrones =
                Arrays.stream(servicePoints)
                        .flatMap(sp -> sp.getDrones().stream())
                        .toList();

        // Keep drones that can handle *all* deliveries
        return allDrones.stream()
                .filter(drone ->
                        records.stream()
                                .allMatch(record -> isDroneAvailable(drone, record))
                )
                .map(DroneAvailability::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAvailableDronesCombined(List<MedDispatchRecord> request) {
        List<String> capabilityDrones = getAvailableDronesCapability(request);
        List<String> dateTimeDrones = getAvailableDronesDateTime(request);
        System.out.println("Capability Drones: " + capabilityDrones);
        System.out.println("DateTime Drones: " + dateTimeDrones);
        capabilityDrones.retainAll(dateTimeDrones);

        return capabilityDrones;
    }

    public List<String> getDroneFromPath(Attribute request) {
        List<Drone> drones = getAllDrones();
        String attributeName = request.getAttribute();
        String attributeOperator = request.getOperator();
        String attributeValue = request.getValue();

        return drones.stream()
                .filter(drone -> {
                    return checkMatchesAttributes(drone, attributeName, attributeValue, attributeOperator);
                })
                // Map the matched drone to its ID as string
                .map(Drone::getId)
                .collect(Collectors.toList());
    }

    public List<String> getDronesFromAttributes(AttributeRequest request) {
        List<Drone> drones = getAllDrones();
        List<Boolean> matchesList = new ArrayList<>();

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
                .map(Drone::getId)
                .collect(Collectors.toList());
    }
    /*public DeliveryPathResponse calcDeliveryPath(DeliveryPathRequest request) {
        String url = ilpEndpoint + "/calculate-delivery-path";
        List<String> dronePaths = restTemplate.postForObject(url, request, List.class);
    }*/

    //----------------------------------------Helper functions------------------------------------------------

    private boolean isDroneAvailable(DroneAvailability drone, MedDispatchRecord record) {
        List<AvailableWindow> availability = drone.getAvailability();

        LocalDate date = record.getDate();
        LocalTime time = record.getTime();

        return availability.stream().anyMatch(window -> {

            String droneDay = window.getDayOfWeek();
            LocalTime droneFrom = LocalTime.parse(window.getFrom());
            LocalTime droneUntil = LocalTime.parse(window.getUntil());

            if (date == null && time != null) {
                return !time.isBefore(droneFrom) && !time.isAfter(droneUntil);
            }
            if (date != null && time == null) {
                return droneDay.equalsIgnoreCase(date.getDayOfWeek().toString());
            }
            if (date == null && time == null) {
                return true;
            }

            // Case 4: Both provided
            return droneDay.equalsIgnoreCase(date.getDayOfWeek().toString())
                    && !time.isBefore(droneFrom)
                    && !time.isAfter(droneUntil);
        });
    }

    // Helper function for attribute matching
    private boolean matches(Object value, String target, String operator) {
        if (value instanceof Number) {
            try {
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

    private boolean checkMatchesAttributes(Drone drone, String attributeName, String targetValue, String operator) {
        Object value = null;
        if (Objects.equals(attributeName, "id")) {
            value = drone.getId();
        } else if (Objects.equals(attributeName, "name")) {
            value = drone.getName();
        } else if (Objects.equals(attributeName, "capacity")) {
            value = drone.getCapability().getCapacity();
        } else if (Objects.equals(attributeName, "cooling")) {
            value = drone.getCapability().getCooling();
        } else if (Objects.equals(attributeName, "heating")) {
            value = drone.getCapability().getHeating();
        } else if (Objects.equals(attributeName, "maxCost")) {
            value = calculateMaxCost(drone);
        }
        return matches(value, targetValue, operator);
    }

    public double calculateMaxCost(Drone drone) {
        Capability capability = drone.getCapability();
        if (capability == null) {
            throw new IllegalArgumentException("Drone capabilities not found");
        }
        return capability.getCostInitial()
                + capability.getCostFinal()
                + (capability.getCostPerMove() * (capability.getMaxMoves()));
    }

    public List<Region> getNoFlyZones() {
        String url = ilpEndpoint + "/restricted-areas";
        Region[] regions = restTemplate.getForObject(url, Region[].class);
        return Arrays.asList(regions);
    }

    private boolean checkForInvalid(List<Boolean> matchesList) {
        for (Boolean match : matchesList) {
            if (!match) {
                matchesList.clear();
                return false;
            }
        }
        return true;
    }
}
