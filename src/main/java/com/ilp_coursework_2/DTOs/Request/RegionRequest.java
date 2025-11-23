package com.ilp_coursework_2.DTOs.Request;

import com.ilp_coursework_2.DTOs.Position;
import com.ilp_coursework_2.DTOs.Region;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class RegionRequest {
    @Valid
    @NotNull(message = "position not found")
    private Position position;
    @Valid
    @NotNull(message = "region not found")
    private Region region;

    public RegionRequest() {}
    public RegionRequest(Position position, Region region) {
        this.position = position;
        this.region = region;
    }
    // Getters and Setters
    public Position getPosition() {return position;}
    public void setPosition(Position position) {this.position = position;}
    public Region getRegion() {return region;}
    public void setRegion(Region region) {this.region = region;}

}
