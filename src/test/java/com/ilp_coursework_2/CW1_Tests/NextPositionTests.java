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
    void nextPosition_output_E() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(0.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946383,\"lng\":-3.192473}", response.getBody());
    }

    @Test
    void nextPosition_output_ENE() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(22.5);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946372,\"lng\":-3.192416}", response.getBody());
    }

    @Test
    void nextPosition_output_NE() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(45.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946339,\"lng\":-3.192367}", response.getBody());
    }

    @Test
    void nextPosition_output_NNE() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(67.5);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.94629,\"lng\":-3.192334}", response.getBody());
    }

    @Test
    void nextPosition_output_N() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(90.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946233,\"lng\":-3.192323}", response.getBody());
    }

    @Test
    void nextPosition_output_NNW() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(112.5);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946176,\"lng\":-3.192334}", response.getBody());
    }

    @Test
    void nextPosition_output_NW() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(135.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946127,\"lng\":-3.192367}", response.getBody());
    }

    @Test
    void nextPosition_output_WNW() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(157.5);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946094,\"lng\":-3.192416}", response.getBody());
    }

    @Test
    void nextPosition_output_W() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(180.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946083,\"lng\":-3.192473}", response.getBody());
    }

    @Test
    void nextPosition_output_WSW() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(202.5);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946094,\"lng\":-3.19253}", response.getBody());
    }

    @Test
    void nextPosition_output_SW() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(225.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946127,\"lng\":-3.192579}", response.getBody());
    }

    @Test
    void nextPosition_output_SSW() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(247.5);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946176,\"lng\":-3.192612}", response.getBody());
    }

    @Test
    void nextPosition_output_S() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(270.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946233,\"lng\":-3.192623}", response.getBody());
    }

    @Test
    void nextPosition_output_SSE() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(292.5);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.94629,\"lng\":-3.192612}", response.getBody());
    }

    @Test
    void nextPosition_output_SE() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(315.0);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946339,\"lng\":-3.192579}", response.getBody());
    }

    @Test
    void nextPosition_output_ESE() {
        NextPositionRequest request = new NextPositionRequest();
        request.setStart(new Position(55.946233,  -3.192473));
        request.setAngle(337.5);
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/nextPosition",
                request,
                String.class);
        assertEquals("{\"lat\":55.946372,\"lng\":-3.19253}", response.getBody());
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
