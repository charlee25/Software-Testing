package com.ilp_coursework_2.CW1_Tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ilp_coursework_2.DTOs.Position;
import com.ilp_coursework_2.DTOs.Request.DistanceRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IsCloseToTests {
    @Autowired
    private MockMvc mockMvc;
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void closeTo_output_correct_false() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                Boolean.class);
        assertEquals(false, response.getBody());
    }

    @Test
    void closeTo_output_correct_true() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946633,  -3.192473));
        request.setPosition2(new Position(55.946617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                Boolean.class);
        assertEquals(true, response.getBody());
    }

    @Test
    void closeTo_code_correct_false() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void closeTo_code_correct_true() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  -3.192473));
        request.setPosition2(new Position(55.946617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void closeTo_output_belowThreshold() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233, -3.192473));
        request.setPosition2(new Position(55.946233, -3.192324));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                Boolean.class);
        assertEquals(true, response.getBody());
    }

    @Test
    void closeTo_output_atThreshold() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233, -3.192473));
        request.setPosition2(new Position(55.946233, -3.192323));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                Boolean.class);
        assertEquals(false, response.getBody());
    }

    @Test
    void closeTo_output_aboveThreshold() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233, -3.192473));
        request.setPosition2(new Position(55.946233, -3.192322));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                Boolean.class);
        assertEquals(false, response.getBody());
    }
    @Test
    void closeTo_code_missingPosition1() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void closeTo_code_missingPosition2() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void closeTo_code_nullPosition1() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(null,  null));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void closeTo_code_nullPosition2() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.942617,  -3.192473));
        request.setPosition2(new Position(null,  null));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void closeTo_code_nullLat() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(null,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void closeTo_code_nullLng() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  null));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void closeTo_code_nullRequest() {
        DistanceRequest request = new DistanceRequest();
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isCloseTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void closeTo_output_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                }
                
                """;
        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(content().string("false"));
    }

    @Test
    void closeTo_code_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                }
                
                """;
        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void closeTo_code_incorrectSyntax() throws Exception {
        String requestBody = """
                {
                    "position1": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "position2": {
                        "lng": -3.192473,
                        "lat": 55.942617
                    }
                
                
                """;
        mockMvc.perform(post("/api/v1/isCloseTo")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
