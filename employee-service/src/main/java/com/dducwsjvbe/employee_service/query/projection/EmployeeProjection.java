package com.dducwsjvbe.employee_service.query.projection;

import com.dducwsjvbe.employee_service.command.data.Employee;
import com.dducwsjvbe.employee_service.command.data.EmployeeRepository;
import com.dducwsjvbe.employee_service.query.model.EmployeeResponse;
import com.dducwsjvbe.employee_service.query.queries.GetFilterEmployee;
import com.dducwsjvbe.employee_service.query.service.searchfilter.SearchService;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class EmployeeProjection {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private SearchService searchService;
    @QueryHandler
    public List<EmployeeResponse> handle(GetFilterEmployee getFilterEmployee) {
        Pageable pageable= PageRequest.of(getFilterEmployee.getPageNo(), getFilterEmployee.getPageSize());
        if (getFilterEmployee.getEmployee()==null || getFilterEmployee.getEmployee().length==0 || Arrays.stream(getFilterEmployee.getEmployee()).allMatch(StringUtils::isEmpty)) {
            Page<String> listIds = employeeRepository.findAllEmployeesIds(pageable,getFilterEmployee.getIsDisciplined());
            List<Employee>listEmployee=employeeRepository.findAllByIds(listIds.getContent());
            return listEmployee.stream().map(employee -> {
                EmployeeResponse employeeResponse = new EmployeeResponse();
                BeanUtils.copyProperties(employee, employeeResponse);
                return employeeResponse;
            }).toList();
        }
        List<Employee>employees=searchService.findByFilter(pageable,getFilterEmployee.getEmployee(),getFilterEmployee.getIsDisciplined());
        return employees.stream().map(employee -> {
            EmployeeResponse employeeResponse = new EmployeeResponse();
            BeanUtils.copyProperties(employee, employeeResponse);
            return employeeResponse;
        }).toList();
    }
}
