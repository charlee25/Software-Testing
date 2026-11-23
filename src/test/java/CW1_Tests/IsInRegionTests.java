package CW1_Tests;

import java.util.ArrayList;
import java.util.List;

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
import com.ilp_coursework_2.DTOs.Region;
import com.ilp_coursework_2.DTOs.Request.RegionRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IsInRegionTests {
    @Autowired
    private MockMvc mockMvc;
    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();


    @Test
    // Point outside polygon - returns false
    void isInRegion_output_correct_false() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region("Central", positions));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals("false", response.getBody());
    }

    @Test
    // Point inside polygon - returns true
    void isInRegion_output_correct_true() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(55.944425, -3.188396));
        request.setRegion(new Region("Central", positions));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals("true", response.getBody());
    }

    @Test
    // Point outside polygon - HTTP 200
    void isInRegion_code_correct_outside() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region("Central", positions));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    // Point inside polygon - HTTP 200
    void isInRegion_code_correct_inside() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(55.944425, -3.188396));
        request.setRegion(new Region("Central", positions));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    // Point inside reverse polygon - returns true
    void isInRegion_output_correct_reversed_true() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(55.944425, -3.188396));
        request.setRegion(new Region("Central", positionsReversed));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals("true", response.getBody());
    }

    @Test
    // Point inside concave polygon - returns true
    void isInRegion_output_correct_concave_true() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(55.9455, -3.186));
        request.setRegion(new Region("Central", positionsConcave));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals("true", response.getBody());
    }

    @Test
    // Point outside concave polygon - returns false
    void isInRegion_output_correct_concave_false() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(55.9435, -3.186));
        request.setRegion(new Region("Central", positionsConcave));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals("false", response.getBody());
    }

    @Test 
    // Point inside concave polygon - HTTP 200
    void isInRegion_code_correct_concave_true() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(55.9455, -3.186));
        request.setRegion(new Region("Central", positionsConcave));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    // Point outside concave polygon - HTTP 200
    void isInRegion_code_correct_concave_false() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(55.9435, -3.186));
        request.setRegion(new Region("Central", positionsConcave));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Missing position - HTTP 400
    @Test
    void isInRegion_code_missingPosition() {
        RegionRequest request = new RegionRequest();
        request.setRegion(new Region("Central", positions));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Missing Region - HTTP 400
    @Test
    void isInRegion_code_missingRegion() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Null region name - HTTP 400
    @Test
    void isInRegion_code_regionName() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region(null, positions));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Null Lat - HTTP 400
    @Test
    void isInRegion_code_nullLat() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(null,  1.234));
        request.setRegion(new Region("Central", positions));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Null Lng - HTTP 400
    @Test
    void isInRegion_code_nullLng() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  null));
        request.setRegion(new Region("Central", positions));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Null positions list - HTTP 400
    @Test
    void isInRegion_code_positionsList() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region("Central", null));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Empty positions list - HTTP 400
    @Test
    void isInRegion_code_emptyPositionsList() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region("Central", positionsEmpty));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Polygon not closed - HTTP 400
    @Test
    void isInRegion_code_polygonNotClosed() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region("Central", positionsNotClosed));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Less than 4 positions - HTTP 400
    @Test
    void isInRegion_code_lessThan4Positions() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region("Central", positionsShort));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Duplicate consecutive verticies - HTTP 400
    @Test
    void isInRegion_code_duplicateVerticies() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region("Central", postionsDuplicatesConsecutive));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Duplicate non-consecutive verticies - HTTP 400
    @Test
    void isInRegion_code_duplicateVerticiesNotConsecutive() {
        RegionRequest request = new RegionRequest();
        request.setPosition(new Position(1.222 ,  1.234));
        request.setRegion(new Region("Central", postionsDuplicatesNotConsecutive));
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Empty body - HTTP 400
    @Test
    void isInRegion_code_nullRequest() {
        RegionRequest request = new RegionRequest();
        var response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/isInRegion",
                request,
                String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void isInRegion_output_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "position": {
                        "lng": 1.234,
                        "lat": 1.222
                    },
                    "region": {
                        "name" : "central",
                        "vertices": [
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat":  55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            }
                        ]
                    }
                }
                
                """;
        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(content().string("false"));
    }

    @Test
    void isInRegion_code_correctSyntax() throws Exception {
        String requestBody = """
                {
                    "position": {
                        "lng": 1.234,
                        "lat": 1.222
                    },
                    "region": {
                        "name" : "central",
                        "vertices": [
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat":  55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            }
                        ]
                    }
                }
                
                """;
        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void isInRegion_code_incorrectSyntax() throws Exception {
        String requestBody = """
                {
                    "position": {
                        "lng": 1.234,
                        "lat": 1.222
                    },
                    "region": {
                        "name" : "central",
                        "vertices": [
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat":  55.942617
                            },
                            {
                                "lng": -3.184319,
                                "lat": 55.946233
                            },
                            {
                                "lng": -3.192473,
                                "lat": 55.946233
                            }
                        ]
                    }
                
                """;
        mockMvc.perform(post("/api/v1/isInRegion")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

 // ------------------------------------------- POSITION LISTS --------------------------------------------------

    List<Position> positions = new ArrayList<>(){
        {
            add(new Position(55.946233 , -3.192473));
            add(new Position(55.942617, -3.192473)); // 1
            add(new Position(55.942617, -3.184319)); // 2
            add(new Position(55.946233, -3.184319)); // 3
            add(new Position(55.946233 , -3.192473));
        }
    };

    List<Position> positionsReversed = new ArrayList<>(){
        {
            add(new Position(55.946233 , -3.192473)); 
            add(new Position(55.946233, -3.184319)); // 3
            add(new Position(55.942617, -3.184319)); // 2
            add(new Position(55.942617, -3.192473)); // 1
            add(new Position(55.946233 , -3.192473));
        }
    };

    List<Position> positionsShort = new ArrayList<>(){
        {
            add(new Position(55.946233 , -3.192473));
            add(new Position(55.942617, -3.192473));
            add(new Position(55.946233 , -3.192473));
        }
    };

    List<Position> positionsNotClosed = new ArrayList<>(){
        {
            add(new Position(55.946233 , -3.192473));
            add(new Position(55.942617, -3.192473));
            add(new Position(55.942617, -3.184319));
            add(new Position(55.946233, -3.184319));
            add(new Position(55.946235 , -3.192486));
        }
    };

    List<Position> positionsConcave = new ArrayList<>(){
        {
            add(new Position(55.946233, -3.192473)); // Top left
            add(new Position(55.946233, -3.184319)); // Top right
            add(new Position(55.944425, -3.184319)); // Inner top right
            add(new Position(55.944425, -3.188396)); // Inner corner
            add(new Position(55.942617, -3.188396)); // Bottom inner
            add(new Position(55.942617, -3.192473)); // Bottom left
            add(new Position(55.946233, -3.192473)); // Close polygon
        }
    };

    List<Position> postionsDuplicatesConsecutive = new ArrayList<>(){
        {
            add(new Position(55.946233 , -3.192473));
            add(new Position(55.942617, -3.192473));
            add(new Position(55.942617, -3.184319)); // duplicate
            add(new Position(55.942617, -3.184319)); // duplicate
            add(new Position(55.946233 , -3.192473));
        }
    };

    List<Position> postionsDuplicatesNotConsecutive = new ArrayList<>(){
        {
            add(new Position(55.946233 , -3.192473));
            add(new Position(55.942617, -3.184319)); // duplicate
            add(new Position(55.942617, -3.192473));
            add(new Position(55.942617, -3.184319)); // duplicate
            add(new Position(55.946233 , -3.192473));
        }
    };

    List<Position> positionsEmpty = new ArrayList<>();
}
