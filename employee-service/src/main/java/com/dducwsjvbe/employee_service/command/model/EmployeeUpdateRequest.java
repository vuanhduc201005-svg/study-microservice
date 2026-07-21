package com.dducwsjvbe.employee_service.command.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateRequest {
    private String firstName;
    private String lastName;
    private String kin;
    private Boolean isDisciplined;
}
