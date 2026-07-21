package com.dducwsjvbe.employee_service.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreatedEvent {
    private String id;

    private String firstName;

    private String lastName;

    private String kin;

    private Boolean isDisciplined;
}
