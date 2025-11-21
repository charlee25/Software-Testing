package com.ilp_coursework_2.DTOs.Request;

import com.ilp_coursework_2.DTOs.MedDispatchRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DeliveryPathRequest {
    @Valid
    @NotNull(message = "Med Dispatch Records not found")
    List<MedDispatchRecord> recordList;

    public DeliveryPathRequest() {
    }
    public DeliveryPathRequest(List<MedDispatchRecord> recordList) {
        this.recordList = recordList;
    }
    //Getters and setters
    public List<MedDispatchRecord> getRecordList() {return recordList;}
    public void setRecordList(List<MedDispatchRecord> recordList) {this.recordList = recordList;}
}
