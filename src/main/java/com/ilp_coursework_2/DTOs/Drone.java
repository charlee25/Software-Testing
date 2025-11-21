package com.ilp_coursework_2.DTOs;

import java.util.List;

public class Drone {
    private String name;
    private String id;
    private Capability capability;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public Capability getCapability() {return capability;}
    public void setCapability(Capability capability) {this.capability = capability;}
}
