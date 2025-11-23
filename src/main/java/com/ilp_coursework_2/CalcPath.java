package com.ilp_coursework_2;

import com.ilp_coursework_2.DTOs.*;
import com.ilp_coursework_2.DTOs.Request.RegionRequest;
import com.ilp_coursework_2.DTOs.Response.DeliveryPathResponse;
import org.springframework.stereotype.Component;
import java.util.List;

import static java.awt.geom.Line2D.linesIntersect;

@Component
public class CalcPath {

    private final CW1_Calculations calculations;

    public CalcPath(CW1_Calculations calculations) {
        this.calculations = calculations;
    }

    public List<Delivery> makeDeliveryList (int DeliveryId, List<Position> flightPath) {
        List<Delivery> deliveryList = new java.util.ArrayList<>();
        Delivery delivery = new Delivery();
        delivery.setDeliveryId(DeliveryId);
        delivery.setFlightPath(flightPath);
        deliveryList.add(delivery);
        return deliveryList;
    }

    public List<DronePath> makeDronePath (String droneId, List<Delivery> deliveryList) {
        List<DronePath> dronePaths = new java.util.ArrayList<>();
        DronePath dronePath = new DronePath();
        dronePath.setDroneId(droneId);
        dronePath.setDeliveries(deliveryList);
        dronePaths.add(dronePath);
        return dronePaths;
    }

    public DeliveryPathResponse makeDeliveryPathResponse(double TotalCost, int TotalMoves, List<DronePath> dronePaths) {
        DeliveryPathResponse response = new DeliveryPathResponse();
        response.setTotalCost(TotalCost);
        response.setTotalMoves(TotalMoves);
        response.setDronePaths(dronePaths);
        return response;
    }

    public boolean pathCrossesZone(Position point1, Position point2, Region zone) {
        List<Position> vertices = zone.getVertices();
        int numVertices = vertices.size();

        for (int i = 0; i < numVertices - 1; i++) {
            Position vertex1 = vertices.get(i);
            Position vertex2 = vertices.get(i + 1);

            if (linesIntersect(
                    point1.getLng(), point1.getLat(),
                    point2.getLng(), point2.getLat(),
                    vertex1.getLng(), vertex1.getLat(),
                    vertex2.getLng(), vertex2.getLat())) {
                return true;
            }
        }
        return false;
    }

    public boolean validNextPosition(List<Region> noFlyZones, Position start, Position nextPosition) {
        boolean validPosition = true;
        for (Region region : noFlyZones) {
            RegionRequest regionRequest = new RegionRequest(nextPosition, region);
            if (pathCrossesZone(start, nextPosition, region)) {
                validPosition = false;
                break;
            }
            if (calculations.isInRegion(regionRequest)) {
                validPosition = false;
                break;
            }
        }
        return validPosition;
    }

    public double getAngle(Position start, Position end) {
        double mathAngle = Math.toDegrees(Math.atan2(
                end.getLat() - start.getLat(),
                end.getLng() - start.getLng())); //Angle to target
        if (mathAngle < 0) {
            mathAngle += 360; //Ensure angle is between 0 and 360
        }
        double angle = (90 - mathAngle + 360) % 360; //Fix angle cuz nextPosition is wrong
        double approxAngle = Math.round(angle / 22.5) * 22.5; //Approximate angle to nearest 22.5
        return approxAngle;
    }
}
