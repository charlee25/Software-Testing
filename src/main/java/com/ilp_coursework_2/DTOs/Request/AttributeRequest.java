package com.ilp_coursework_2.DTOs.Request;

import com.ilp_coursework_2.DTOs.Attribute;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AttributeRequest {
    @Valid
    @NotNull(message = "Input not found")
    List<Attribute> attributes;

    public AttributeRequest() {
    }
    public AttributeRequest(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    //Getters and setters
    public List<Attribute> getAttributes() {return attributes;}
    public void setAttributes(List<Attribute> attributes) {this.attributes = attributes;}
}
