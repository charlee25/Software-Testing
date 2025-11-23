package com.ilp_coursework_2;

import com.ilp_coursework_2.DTOs.*;
import com.ilp_coursework_2.DTOs.Availability.AvailableWindow;
import com.ilp_coursework_2.DTOs.Availability.DroneAvailability;
import com.ilp_coursework_2.DTOs.Availability.ServicePoint;
import com.ilp_coursework_2.DTOs.Request.AttributeRequest;
import com.ilp_coursework_2.DTOs.MedDispatchRecord;
import com.ilp_coursework_2.DTOs.Request.DistanceRequest;
import com.ilp_coursework_2.DTOs.Request.NextPositionRequest;
import com.ilp_coursework_2.DTOs.Response.DeliveryPathResponse;
import com.ilp_coursework_2.Enums.CaseType;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.yaml.snakeyaml.util.Tuple;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DroneService {

    private final RestTemplate restTemplate;
    private final String ilpEndpoint;
    private final CW1_Calculations calculations;
    private final CalcPath calcPath;

    public DroneService(
            RestTemplate restTemplate,
            String ilpEndpoint,
            CW1_Calculations calculations,
            CalcPath calcPath
    ) {
        this.restTemplate = restTemplate;
        this.ilpEndpoint = ilpEndpoint;
        this.calculations = calculations;
        this.calcPath = calcPath;
    }
    double stepDistance = 0.00015;

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

                        //If drone max moves is less than required min steps, return false
                        if (drone.getCapability().getMaxMoves() < calcMinSteps(
                                getServicePointLocationForDrone(drone.getId()), record.getDelivery())) {
                            return false;
                        }

                        boolean capacityMatches = droneCapacity >= req.getCapacity();
                        boolean heatingMatches = !req.getHeating() || droneHeating;
                        boolean coolingMatches = !req.getCooling() || droneCooling;
                        boolean costMatches = req.getMaxCost() != null || droneCost <= req.getMaxCost();

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
    public DeliveryPathResponse calcDeliveryPath(List<MedDispatchRecord> records) {
        DroneAssignmentResult assignmentResult = assignDrones(records);
        int totalMoves = 0;
        double totalCost = 0;
        List<DronePath> dronePaths = new ArrayList<>();

        if (assignmentResult.getCaseType() == CaseType.MULTIPLE_FLIGHTS_ONE_DRONE){
            //Only one Drone
            String droneId = assignmentResult.getAssignments().keySet().iterator().next();
            List<MedDispatchRecord> assignedRecords = assignmentResult.getAssignments().get(droneId);
            List<Delivery> deliveryList = getDeliveryList(assignedRecords, droneId)._1();

            totalMoves += getDeliveryList(assignedRecords, droneId)._2();
            dronePaths = calcPath.makeDronePath(droneId, deliveryList);
            totalCost = calculateMaxCost(getDroneFromID(droneId));

        } else {
            //Multiple Drones
            for (Map.Entry<String, List<MedDispatchRecord>> entry : assignmentResult.getAssignments().entrySet()) {
                String droneId = entry.getKey();
                List<MedDispatchRecord> assignedRecords = entry.getValue();
                List<Delivery> deliveryList = getDeliveryList(assignedRecords, droneId)._1();

                totalMoves += getDeliveryList(assignedRecords, droneId)._2();
                dronePaths = calcPath.makeDronePath(droneId, deliveryList);
                totalCost = calculateMaxCost(getDroneFromID(droneId));
            }
        }
        return calcPath.makeDeliveryPathResponse(totalCost, totalMoves, dronePaths);
    }

    //----------------------------------------Helper functions------------------------------------------------

    private Tuple<List<Delivery>, Integer> getDeliveryList (List<MedDispatchRecord> assignedRecords, String droneId ){
        Delivery delivery;
        int deliveryId;
        int returnId = -1;
        int totalMoves = 0;
        Position start;
        Position end;
        List<Position> deliverPath;
        List<Delivery> deliveryList = new ArrayList<>();

        for (MedDispatchRecord record : assignedRecords) {
            //Journey from SP to drop off point
            deliveryId = record.getId();
            start = getServicePointLocationForDrone(droneId);
            end = record.getDelivery();
            System.out.println(end.getLat());
            deliverPath = calculatePath(start, end);
            totalMoves += deliverPath.size() - 1;

            delivery = new Delivery(deliveryId, deliverPath);
            deliveryList.add(delivery);

            //Journey from drop off point to SP
            deliveryId = returnId--;
            start = deliverPath.getLast();
            end = getServicePointLocationForDrone(droneId);
            deliverPath = calculatePath(start, end);
            totalMoves += deliverPath.size() - 1;

            delivery = new Delivery(deliveryId, deliverPath);
            deliveryList.add(delivery);
        }
        return new Tuple<>(deliveryList, totalMoves);
    }
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
    public DroneAssignmentResult assignDrones(List<MedDispatchRecord> records) {
        //case 1 — One drone can do all records in multiple flights
        List<String> multiFlight = getAvailableDronesCombined(records);
        if (!multiFlight.isEmpty()) {
            Map<String, List<MedDispatchRecord>> map = new HashMap<>();
            map.put(multiFlight.getFirst(), records);
            return new DroneAssignmentResult(CaseType.MULTIPLE_FLIGHTS_ONE_DRONE, map);
        }

        //case 2 — Multiple drones required
        Map<String, List<MedDispatchRecord>> assignments = new HashMap<>();
        for (MedDispatchRecord record : records) {
            List<MedDispatchRecord> single = List.of(record);
            List<String> available = getAvailableDronesCombined(single);

            if (available.isEmpty()) {
                throw new RuntimeException("No drone can deliver record id =" + record.getId());
            }
            String droneId = available.getFirst();

            if (!assignments.containsKey(droneId)) {
                assignments.put(droneId, new ArrayList<>());
            }
            assignments.get(droneId).add(record);
        }
        return new DroneAssignmentResult(CaseType.MULTIPLE_DRONES, assignments);
    }

    public List<Position> calculatePath(Position start, Position end) {
        DistanceRequest request = new DistanceRequest(start, end);
        List<Position> deliveryPathPoints = new java.util.ArrayList<>();
        List<Region> noFlyZones = getNoFlyZones();
        double approxAngle = 0.0;

        while (!calculations.isCloseTo(request)) {
            //double distance = calculations.getDistance(request); //Distance to target
            approxAngle = calcPath.getAngle(start, end);

            //Get next Position
            NextPositionRequest nextPositionRequest = new NextPositionRequest(start, approxAngle);
            Position nextPosition = calculations.nextPosition(nextPositionRequest);

            if (calcPath.validNextPosition(noFlyZones, start, nextPosition)) {
                deliveryPathPoints.add(nextPosition);
                start = nextPosition;
                request = new DistanceRequest(start, end);
            } else {
                for (int i = 0; i < 15; i++) {
                    approxAngle = (approxAngle + 22.5) % 360;
                    nextPositionRequest = new NextPositionRequest(start, approxAngle);
                    nextPosition = calculations.nextPosition(nextPositionRequest);
                    if (calcPath.validNextPosition(noFlyZones, start, nextPosition)) {
                        deliveryPathPoints.add(nextPosition);
                        start = nextPosition;
                        request = new DistanceRequest(start, end);
                        break;
                    }
                }
            }
        }
        deliveryPathPoints.add(deliveryPathPoints.getLast()); //Add final position again to hover
        return deliveryPathPoints;
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
    private Position getServicePointLocationForDrone(String droneId) {
        //Find which service point the drone belongs to
        String urlDrones = ilpEndpoint + "/drones-for-service-points";
        ServicePoint[] droneServicePoints = restTemplate.getForObject(urlDrones, ServicePoint[].class);
        if (droneServicePoints == null) {
            throw new RuntimeException("No drones-for-service-points data found");
        }

        Integer spIdForDrone = null;

        for (ServicePoint sp : droneServicePoints) {
            if (sp.getDrones() == null) continue;
            for (DroneAvailability da : sp.getDrones()) {
                if (droneId.equals(da.getId())) {
                    spIdForDrone = sp.getServicePointId();
                    break;
                }
            }
            if (spIdForDrone != null) break;
        }

        if (spIdForDrone == null) {
            throw new RuntimeException("Drone " + droneId + " not assigned to any service point");
        }

        // Step 2: Look up the coordinates of that service point
        String urlServicePoints = ilpEndpoint + "/service-points";
        ServicePointWithLocation[] allServicePoints = restTemplate.getForObject(urlServicePoints, ServicePointWithLocation[].class);
        if (allServicePoints == null) {
            throw new RuntimeException("No service-points data found");
        }

        for (ServicePointWithLocation sp : allServicePoints) {
            if (sp.getId().equals(spIdForDrone)) {
                return sp.getLocation(); // returns Position {lat, lng}
            }
        }

        throw new RuntimeException("Service point id " + spIdForDrone + " not found in service-points");
    }

    private double calcMinSteps(Position start, Position end) {
        int steps = 0;
        double distance = calculations.CalculateDistance(start, end);
        return (distance / stepDistance);
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
