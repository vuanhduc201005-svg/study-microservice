package com.dducwsjvbe.employee_service.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeCommand {
    @TargetAggregateIdentifier
    private String id;
    private String firstName;
    private String lastName;
    private String kin;
    private Boolean isDisciplined;
}
