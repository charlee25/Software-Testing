package CW1_Tests;

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
class DistanceToTests {
    @Autowired
    private MockMvc mockMvc;
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void distanceTo_output_correct() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals("0.003616", response.getBody());
    }

    @Test
    void distanceTo_output_symmetric() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.942617,  -3.192473));
        request.setPosition2(new Position(55.946233,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals("0.003616", response.getBody());
    }

    @Test
    void distanceTo_code_correct() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void distanceTo_output_identical_pos() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  -3.192473));
        request.setPosition2(new Position(55.946233,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(0.000000, Double.parseDouble(response.getBody()), 0.0000000001);
    }

    @Test
    void distanceTo_output_lng_diff() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  -3.192470));
        request.setPosition2(new Position(55.946233,  -3.192475));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(0.000005, Double.parseDouble(response.getBody()), 0.0000000001);
    }

    @Test 
    void distanceTo_output_lat_diff() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946230,  -3.192470));
        request.setPosition2(new Position(55.946235,  -3.192470));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(0.000005, Double.parseDouble(response.getBody()), 0.0000000001);
    }

    @Test 
    void distanceTo_output_neg_coords() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(-55.946233,  -3.192473));
        request.setPosition2(new Position(-55.942617,  -3.192475));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals("0.003616", response.getBody());
    }

    @Test
    void distanceTo_code_missingPosition1() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void distanceTo_code_missingPosition2() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void distanceTo_code_nullPosition1() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(null,  null));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void distanceTo_code_nullPosition2() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.942617,  -3.192473));
        request.setPosition2(new Position(null,  null));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void distanceTo_code_nullLat() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(null,  -3.192473));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void distanceTo_code_nullLng() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(55.946233,  null));
        request.setPosition2(new Position(55.942617,  -3.192473));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void distanceTo_code_nullRequest() {
        DistanceRequest request = new DistanceRequest();
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test 
    void distanceTo_output_rounding() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(-55.946233,  -3.192473));
        request.setPosition2(new Position(-55.946233,  -3.1924736));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(0.000001, Double.parseDouble(response.getBody()), 0.0000000001);
    }

    @Test 
    void distanceTo_output_smallDistance() {
        DistanceRequest request = new DistanceRequest();
        request.setPosition1(new Position(-55.946233,  -3.192473));
        request.setPosition2(new Position(-55.946233,  -3.1924734));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/distanceTo",
                request,
                String.class);
        assertEquals(0.000000, Double.parseDouble(response.getBody()), 0.0000000001);
    }

    @Test
    void distanceTo_output_correctSyntax() throws Exception {
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
        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType("application/json")
                        .content(requestBody))
                        .andExpect(content().string("0.003616")); 
    }

    @Test
    void distanceTo_code_correctSyntax() throws Exception {
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
        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void distanceTo_code_incorrectSyntax() throws Exception {
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
        mockMvc.perform(post("/api/v1/distanceTo")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
