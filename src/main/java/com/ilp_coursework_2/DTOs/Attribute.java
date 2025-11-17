package com.ilp_coursework_2.DTOs;

import jakarta.validation.constraints.NotNull;

public class Attribute {
    @NotNull(message = "attribute not found")
    private String attribute;
    @NotNull(message = "operator not found")
    private String operator;
    @NotNull(message = "value not found")
    private String value;

    public Attribute(String attribute, String operator, String value) {
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
    }
    //Getters and Setters
    public String getAttribute() {return attribute;}
    public void setAttribute(String attribute) {this.attribute = attribute;}
    public String getOperator() {return operator;}
    public void setOperator(String operator) {this.operator = operator;}
    public String getValue() {return value;}
    public void setValue(String value) {this.value = value;}

}
