package com.dducwsjvbe.employee_service.command.controller;

import com.dducwsjvbe.employee_service.command.command.CreateEmployeeCommand;
import com.dducwsjvbe.employee_service.command.command.DeleteEmployeeCommand;
import com.dducwsjvbe.employee_service.command.command.UpdateEmployeeCommand;
import com.dducwsjvbe.employee_service.command.model.EmployeeCreateRequest;
import com.dducwsjvbe.employee_service.command.model.EmployeeUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@Slf4j(topic = "Employee-Command-Controller")
@Tag(name = "Employee Command Controller", description = "API for managing employee data")
public class EmployeeCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @Operation(method = "POST", summary = "create employee", description = "create employee")
    @PostMapping
    public String createEmployee(@Valid @RequestBody EmployeeCreateRequest employeeCreateRequest) {
        log.info("createEmployee");
        CreateEmployeeCommand createEmployeeCommand = new CreateEmployeeCommand(
                UUID.randomUUID().toString(),
                employeeCreateRequest.getFirstName(),
                employeeCreateRequest.getLastName(),
                employeeCreateRequest.getKin(),
                Boolean.FALSE
        );
        return commandGateway.sendAndWait(createEmployeeCommand);
    }

    @Operation(method = "PUT",summary = "update employee", description = "update employee")
    @PutMapping("/{employeeId}")
    private String updateEmployee(@PathVariable String employeeId, @RequestBody EmployeeUpdateRequest employeeUpdateRequest) {
        log.info("updateEmployee");
        UpdateEmployeeCommand updateEmployeeCommand = new UpdateEmployeeCommand(
                employeeId,
                employeeUpdateRequest.getFirstName(),
                employeeUpdateRequest.getLastName(),
                employeeUpdateRequest.getKin(),
                employeeUpdateRequest.getIsDisciplined()
        );
        return commandGateway.sendAndWait(updateEmployeeCommand);
    }

    @Operation(method = "DELETE",summary = "delete employee", description = "delete employee")
    @DeleteMapping("/employeeId")
    private String deleteEmployee(@PathVariable String employeeId) {
        log.info("deleteEmployee");
        DeleteEmployeeCommand deleteEmployeeCommand = new DeleteEmployeeCommand(
                employeeId
        );
        return commandGateway.sendAndWait(deleteEmployeeCommand);
    }

}
