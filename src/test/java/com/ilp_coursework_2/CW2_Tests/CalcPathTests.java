package com.ilp_coursework_2.CW2_Tests;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import com.ilp_coursework_2.DTOs.Availability.AvailableWindow;
import com.ilp_coursework_2.DTOs.Availability.DroneAvailability;
import com.ilp_coursework_2.DTOs.Availability.ServicePoint;
import com.ilp_coursework_2.DTOs.Capability;
import com.ilp_coursework_2.DTOs.Drone;
import com.ilp_coursework_2.DTOs.MedDispatchRecord;
import com.ilp_coursework_2.DTOs.Position;
import com.ilp_coursework_2.DTOs.Region;
import com.ilp_coursework_2.DTOs.Requirements;
import com.ilp_coursework_2.DTOs.Response.DeliveryPathResponse;
import com.ilp_coursework_2.DTOs.ServicePointWithLocation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class CalcPathTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockitoBean
    private RestTemplate restTemplate;
    
    private Drone drone;
    private ServicePoint servicePoint;
    private ServicePointWithLocation location;
    private List<MedDispatchRecord> records;

    @BeforeEach
    void setUp() {

        // Availability
        AvailableWindow window = new AvailableWindow();
        window.setDayOfWeek("MONDAY");
        window.setFrom("00:00");
        window.setUntil("23:59");

        DroneAvailability availability = new DroneAvailability();
        availability.setId("1");
        availability.setAvailability(List.of(window));

        DroneAvailability availability2 = new DroneAvailability();
        availability2.setId("2");
        availability2.setAvailability(List.of(window));

        // Service point
        servicePoint = new ServicePoint();
        servicePoint.setServicePointId(1);
        servicePoint.setDrones(List.of(availability, availability2));

        // Service point location
        location = new ServicePointWithLocation();
        location.setId(1);
        location.setLocation(new Position(55.9440, -3.1880));

        mockServiceData();
    }


    @Test
    void deliveryPath_oneDrone_oneRecord() {

        drone = create_drone("1", 8.0, false, false);

        MedDispatchRecord record = create_record(1, 1.0, 55.9443, -3.1880, false, false);
        records = List.of(record);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone});
        
        var response = testRestTemplate.postForEntity(
                "/api/v1/calcDeliveryPath",
                records,
                DeliveryPathResponse.class
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("1", response.getBody() // Assigns drone 1
                        .getDronePaths().get(0).getDroneId()),
                () -> assertEquals(2, response.getBody() // Returns 2 delivery flights; from and to service point
                        .getDronePaths().get(0).getDeliveries().size()),
                () -> assertEquals(1, response.getBody() // Returns delivery ID 1
                        .getDronePaths().get(0).getDeliveries().get(0).getDeliveryId())
        );
    }

    @Test
    void deliveryPath_oneDrone_multipleRecords_oneFlight() {

        drone = create_drone("1", 8.0, false, false);

        MedDispatchRecord record1 = create_record(1, 1.0, 55.9441, -3.1869, false, false);
        MedDispatchRecord record2 = create_record(2, 1.0, 55.945, -3.1861, false, false);
        records = List.of(record1, record2);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone});
        
        var response = testRestTemplate.postForEntity(
                "/api/v1/calcDeliveryPath",
                records,
                DeliveryPathResponse.class
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("1", response.getBody() // Assigns drone 1
                        .getDronePaths().get(0).getDroneId()),
                () -> assertEquals(3, response.getBody() // 3 delivery entries in drone's path; from SP, to next delivery, back to SP
                        .getDronePaths().get(0).getDeliveries().size()),
                () -> assertEquals(1, response.getBody() // First delivery is ID 1
                        .getDronePaths().get(0).getDeliveries().get(0).getDeliveryId()),
                () -> assertEquals(2, response.getBody() // Second delivery is ID 2
                        .getDronePaths().get(0).getDeliveries().get(1).getDeliveryId())
        );
    }

    @Test
    void deliveryPath_oneDrone_multipleRecords_multipleFlights() {

        drone = create_drone("1", 8.0, false, false);

        MedDispatchRecord record1 = create_record(1, 6.0, 55.9428, -3.1842, false, false);
        MedDispatchRecord record2 = create_record(2, 6.0, 55.9496, -3.1867, false, false);
        records = List.of(record1, record2);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone});
        
        var response = testRestTemplate.postForEntity(
                "/api/v1/calcDeliveryPath",
                records,
                DeliveryPathResponse.class
        );

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("1", response.getBody() // Assigns drone 1
                        .getDronePaths().get(0).getDroneId()),
                () -> assertEquals(4, response.getBody() // 4 delivery entries in drone's path; from SP, back to SP x2
                        .getDronePaths().get(0).getDeliveries().size()),
                () -> assertEquals(1, response.getBody() // First delivery is ID 1
                        .getDronePaths().get(0).getDeliveries().get(0).getDeliveryId()),
                () -> assertEquals(2, response.getBody() // Second delivery is ID 2
                        .getDronePaths().get(0).getDeliveries().get(2).getDeliveryId())
        );
    }

    @Test
    void deliveryPath_multipleDrone_multipleRecords() {

        Drone drone1 = create_drone("1", 6.0, true, false);
        Drone drone2 = create_drone("2", 6.0, false, true);

        MedDispatchRecord record1 = create_record(1, 3.0, 55.9428, -3.1842, true, false);
        MedDispatchRecord record2 = create_record(2, 3.0, 55.9496, -3.1867, false, true);
        records = List.of(record1, record2);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone1, drone2});
        
        var response = testRestTemplate.postForEntity(
                "/api/v1/calcDeliveryPath",
                records,
                DeliveryPathResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        var dronePaths = response.getBody().getDronePaths();

        var drone1Path = dronePaths.stream()
                .filter(path -> "1".equals(path.getDroneId()))
                .findFirst()
                .orElse(null);
        var drone2Path = dronePaths.stream()
                .filter(path -> "2".equals(path.getDroneId()))
                .findFirst()
                .orElse(null);

        assertAll(
                () -> assertNotNull(drone1Path),
                () -> assertNotNull(drone2Path),
                () -> assertEquals(2, dronePaths.size()),
                () -> assertEquals(1, drone1Path.getDeliveries().get(0).getDeliveryId()),
                () -> assertEquals(2, drone2Path.getDeliveries().get(0).getDeliveryId()),
                
                // 4 total delivery flights across both drones
                () -> assertEquals(4, response.getBody()
                        .getDronePaths()
                        .stream()
                        .mapToInt(dronePath -> dronePath.getDeliveries().size())
                        .sum())
        );
    }

    @Test
    void deliveryPath_droneAssignmentFailure() {

        Drone drone1 = create_drone("1", 6.0, true, false);

        // record 2 does not have a drone that can deliver it
        MedDispatchRecord record1 = create_record(1, 3.0, 55.9428, -3.1842, true, false);
        MedDispatchRecord record2 = create_record(2, 3.0, 55.9496, -3.1867, false, true);
        records = List.of(record1, record2);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone1});
        
        var response = testRestTemplate.postForEntity(
                "/api/v1/calcDeliveryPath",
                records,
                DeliveryPathResponse.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    private Drone create_drone(String id, Double capacity, boolean heating, boolean cooling) {

        Capability capability = new Capability();
        capability.setCooling(cooling);
        capability.setHeating(heating);
        capability.setCapacity(capacity);
        capability.setMaxMoves(1000);
        capability.setCostPerMove(0.02);
        capability.setCostInitial(1.0);
        capability.setCostFinal(3.0);

        Drone drone = new Drone();
        drone.setId(id);
        drone.setName("Test Drone " + id);
        drone.setCapability(capability);

        return drone;
    }

    private MedDispatchRecord create_record(int id, double capacity, double lat, double lng, boolean heating, boolean cooling) {

        Requirements requirements = new Requirements();
        requirements.setCapacity(capacity);
        requirements.setCooling(cooling);
        requirements.setHeating(heating);
        requirements.setMaxCost(100.0);

        Position deliveryPosition = new Position(lat, lng);

        MedDispatchRecord record = new MedDispatchRecord(id, requirements, deliveryPosition);
        return record;
    }

    private void mockServiceData() {
        when(restTemplate.getForObject(
                endsWith("/drones-for-service-points"),
                eq(ServicePoint[].class)
        )).thenReturn(new ServicePoint[]{servicePoint});

        when(restTemplate.getForObject(
                endsWith("/service-points"),
                eq(ServicePointWithLocation[].class)
        )).thenReturn(new ServicePointWithLocation[]{location});

        when(restTemplate.getForObject(
                endsWith("/restricted-areas"),
                eq(Region[].class)
        )).thenReturn(new Region[]{});
    }
}