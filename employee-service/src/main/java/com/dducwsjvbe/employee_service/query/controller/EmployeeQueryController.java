package com.dducwsjvbe.employee_service.query.controller;

import com.dducwsjvbe.employee_service.query.model.EmployeeResponse;
import com.dducwsjvbe.employee_service.query.queries.GetFilterEmployee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@Slf4j(topic = "Employee-Query-Controller")
@Tag(name = "Employee Query Controller", description = "API for querying employee data")
public class EmployeeQueryController {
    @Autowired
    private QueryGateway queryGateway;

    @Operation(method = "GET", summary = "search filter", description = "search filter")
    @GetMapping
    public List<EmployeeResponse> queryFilterEmployee(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                      @RequestParam(defaultValue = "10", required = false) int pageSize,
                                                      @RequestParam(required = false) String[] employee,
                                                      @RequestParam Boolean isDisciplined
    ) {
        log.info("queryFilterEmployee");
        GetFilterEmployee getFilterEmployee = new GetFilterEmployee(pageNo, pageSize, employee, isDisciplined);
        List<EmployeeResponse> result = queryGateway.query(getFilterEmployee, ResponseTypes.multipleInstancesOf(EmployeeResponse.class)).join();
        return result;
    }
}
