package com.ilp_coursework_2;

import com.ilp_coursework_2.DTOs.Position;
import com.ilp_coursework_2.DTOs.Region;
import com.ilp_coursework_2.DTOs.Request.DistanceRequest;
import com.ilp_coursework_2.DTOs.Request.NextPositionRequest;
import com.ilp_coursework_2.controllers.CalculationController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.awt.geom.Line2D.linesIntersect;

public class CalcPath {
    RestTemplate restTemplate = new RestTemplate();
    EndpointConfig ilpEndpoint = new EndpointConfig();
    double stepDistance = 0.00015;
   public boolean pathCrossesZone (Position point1, Position point2) {
       DroneService droneService = new DroneService(restTemplate, ilpEndpoint.ilpEndpoint());
       List<Region> noFlyZones = droneService.getNoFlyZones();

       for (Region zone : noFlyZones) {
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
       }
       return false;
   }
   public List<Position> calculatePath (Position start, Position end) {
       DistanceRequest request = new DistanceRequest(start, end);
       CalculationController calculationController = new CalculationController();

       while (!calculationController.isCloseTo(request).getBody()) {
           double distance = calculationController.getDistance(request).getBody(); //Distance to target
           double angle = Math.toDegrees(Math.atan2(end.getLat() - start.getLat(), end.getLng() - start.getLng())); //Angle to target

           //Get next Position
           NextPositionRequest nextPositionRequest = new NextPositionRequest(start, angle);
           Position nextPosition = (Position) calculationController.nextPosition(nextPositionRequest).getBody();
       }
   }
}
