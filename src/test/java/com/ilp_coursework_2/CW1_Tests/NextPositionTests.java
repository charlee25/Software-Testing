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
import com.ilp_coursework_2.DTOs.Request.NextPositionRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NextPositionTests {
    @Autowired
    private MockMvc mockMvc;
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void nextPosition_output_0() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(0.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946383,\"lng\":-3.192473}", response.getBody());
    }

    @Test
    void nextPosition_output_45() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946339,\"lng\":-3.192367}", response.getBody());
    }

    @Test
    void nextPosition_output_90() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(90.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946233,\"lng\":-3.192323}", response.getBody());
    }

    @Test
    void nextPosition_output_180() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(180.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946083,\"lng\":-3.192473}", response.getBody());
    }

    @Test
    void nextPosition_output_270() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(270.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946233,\"lng\":-3.192623}", response.getBody());
    }

    @Test
    void nextPosition_output_360() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(360.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946383,\"lng\":-3.192473}", response.getBody());
    }

    @Test
    void nextPosition_code_correct() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void nextPosition_output_negPosition() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(-55.946233,  -3.192473));
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":-55.946127,\"lng\":-3.192367}", response.getBody());
    }

    @Test
    void nextPosition_code_negAngle() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(-45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test 
    void nextPosition_code_largeAngle() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(420.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test 
    void nextPosition_code_invalidIncrement() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(11.25);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void nextPosition_code_missingStart() {
        NextPositionRequest request = new NextPositionRequest();
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void nextPosition_code_missingAngle() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void nextPosition_code_nullLat() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(null,  -3.192473));
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void nextPosition_code_nullLng() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  null));
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void nextPosition_code_nullAngle() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(null);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void nextPosition_code_nullRequest() {
        NextPositionRequest request = new NextPositionRequest();
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void nextPosition_output_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "start": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "angle": 45.0
                }
                
                """;
        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(content().string("{\"lat\":55.946339,\"lng\":-3.192367}"));
    }

    @Test
    void nextPosition_code_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "start": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "angle": 45.0
                }
                
                """;
        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void nextPosition_code_incorrectSyntax() throws Exception {
        String requestBody = """
                {
                    "start": {
                        "lng": -3.192473,
                        "lat": 55.946233
                    },
                    "angle": 45.0
              
                """;
        mockMvc.perform(post("/api/v1/nextPosition")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
