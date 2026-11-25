package com.ilp_coursework_2.CW2_Tests;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ilp_coursework_2.DTOs.ServicePointWithLocation;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class GeoJsonTests {
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
    void geoJson_oneRecord() {

        drone = create_drone("1", 8.0, false, false);

        MedDispatchRecord record = create_record(1, 1.0, 55.9443, -3.1880, false, false);
        records = List.of(record);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone});
        
        ResponseEntity<Map> response = testRestTemplate.postForEntity(
            "/api/v1/calcDeliveryPathAsGeoJson",
            records,
            Map.class
        );

        GeoJSON_assertions(response);
    }

    @Test 
    void geoJson_oneDrone_multipleRecords_oneFlight() {

        drone = create_drone("1", 8.0, false, false);

        MedDispatchRecord record1 = create_record(1, 1.0, 55.9441, -3.1869, false, false);
        MedDispatchRecord record2 = create_record(2, 1.0, 55.945, -3.1861, false, false);
        records = List.of(record1, record2);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone});
        
        ResponseEntity<Map> response = testRestTemplate.postForEntity(
            "/api/v1/calcDeliveryPathAsGeoJson",
            records,
            Map.class
        );

        GeoJSON_assertions(response);
    }

    @Test 
    void geoJson_incompatible_oneDrone() {

        drone = create_drone("1", 8.0, false, false);

        MedDispatchRecord record1 = create_record(1, 6.0, 55.9428, -3.1842, false, false);
        MedDispatchRecord record2 = create_record(2, 6.0, 55.9496, -3.1867, false, false);
        records = List.of(record1, record2);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone});
        
        ResponseEntity<Map> response = testRestTemplate.postForEntity(
            "/api/v1/calcDeliveryPathAsGeoJson",
            records,
            Map.class
        );

        GeoJSON_assertions_incompatible(response);
    }

    @Test 
    void geoJson_incompatible_multipleDrones() {

        Drone drone1 = create_drone("1", 6.0, true, false);
        Drone drone2 = create_drone("2", 6.0, false, true);

        MedDispatchRecord record1 = create_record(1, 3.0, 55.9428, -3.1842, true, false);
        MedDispatchRecord record2 = create_record(2, 3.0, 55.9496, -3.1867, false, true);
        records = List.of(record1, record2);

        when(restTemplate.getForObject(
                endsWith("/drones"),
                eq(Drone[].class)
        )).thenReturn(new Drone[]{drone1, drone2});
        
        ResponseEntity<Map> response = testRestTemplate.postForEntity(
            "/api/v1/calcDeliveryPathAsGeoJson",
            records,
            Map.class
        );

        GeoJSON_assertions_incompatible(response);
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

    private void GeoJSON_assertions(ResponseEntity<Map> response) {
        assertNotNull(response.getBody());

        Map<String, Object> geoJson = response.getBody();
        Map<String, Object> geometry = (Map<String, Object>) geoJson.get("geometry");
        Map<String, Object> properties = (Map<String, Object>) geoJson.get("properties");
        List<List<Number>> coordinates = (List<List<Number>>) geometry.get("coordinates");

        assertAll(
            () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
            
            () -> assertEquals("Feature", geoJson.get("type")),
            () -> assertNotNull(geoJson.get("geometry")),
            () -> assertNotNull(geoJson.get("properties")),

            () -> assertEquals("LineString", geometry.get("type")),
            () -> assertNotNull(geometry.get("coordinates")),

            () -> assertEquals("1", properties.get("droneId")),
            () -> assertTrue(coordinates.size() >= 2)
        );

        for (List<Number> coordinate : coordinates) {
            assertEquals(2, coordinate.size());

            assertNotNull(coordinate.get(0)); // lng
            assertNotNull(coordinate.get(1)); // lat
        }
    }

    private void GeoJSON_assertions_incompatible(ResponseEntity<Map> response) {
        assertNotNull(response.getBody());
        Map<String, Object> geoJson = response.getBody();
        Map<String, Object> properties = (Map<String, Object>) geoJson.get("properties");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cannot perform delivery in a single continuous flight", properties.get("message"));
    }
}
