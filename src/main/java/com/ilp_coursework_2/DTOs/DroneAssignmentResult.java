package com.ilp_coursework_2.DTOs;

import com.ilp_coursework_2.Enums.CaseType;

import java.util.List;
import java.util.Map;

public class DroneAssignmentResult {

    private final CaseType caseType;
    private final Map<String, List<MedDispatchRecord>> assignments;

    public DroneAssignmentResult(CaseType caseType,
                                 Map<String, List<MedDispatchRecord>> assignments) {
        this.caseType = caseType;
        this.assignments = assignments;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    public Map<String, List<MedDispatchRecord>> getAssignments() {
        return assignments;
    }
}