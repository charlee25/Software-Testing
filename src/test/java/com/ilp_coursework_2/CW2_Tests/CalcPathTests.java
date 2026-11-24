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

        // Service point
        servicePoint = new ServicePoint();
        servicePoint.setServicePointId(1);
        servicePoint.setDrones(List.of(availability));

        // Service point location
        location = new ServicePointWithLocation();
        location.setId(1);
        location.setLocation(new Position(55.9440, -3.1880));
    }


    @Test
    void deliveryPath_oneDroneOneFlight() {

        drone = create_drone("1", 8.0);

        MedDispatchRecord record = create_record(1, 1.0, 55.9443, -3.1880 );
        records = List.of(record);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone});

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
                () -> assertEquals(1, response.getBody() // Returns 1 drone patth
                        .getDronePaths().size()),
                () -> assertEquals(1, response.getBody() // Returns delivery ID 1
                        .getDronePaths().get(0).getDeliveries().get(0).getDeliveryId())
        );
    }

    @Test
    void deliveryPath_oneDrone_multipleFlights() {

        drone = create_drone("1", 8.0);

        MedDispatchRecord record1 = create_record(1, 1.0, 55.9443, -3.1880 );
        MedDispatchRecord record2 = create_record(2, 1.0, 55.9450, -3.1890 );
        records = List.of(record1, record2);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone});

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
                () -> assertEquals(2, response.getBody() // Returns 2 drone paths
                        .getDronePaths().size()),
                () -> assertEquals(1, response.getBody() // First delivery is ID 1
                        .getDronePaths().get(0).getDeliveries().get(0).getDeliveryId())
        );
    }

    private Drone create_drone(String id, Double capacity) {

        Capability capability = new Capability();
        capability.setCooling(false);
        capability.setHeating(false);
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

    private MedDispatchRecord create_record(int id, double capacity, double lat, double lng) {
        Requirements requirements = new Requirements();
        requirements.setCapacity(capacity);
        requirements.setCooling(false);
        requirements.setHeating(false);
        requirements.setMaxCost(100.0);

        Position deliveryPosition = new Position(lat, lng);

        MedDispatchRecord record = new MedDispatchRecord(id, requirements, deliveryPosition);
        return record;
    }

}