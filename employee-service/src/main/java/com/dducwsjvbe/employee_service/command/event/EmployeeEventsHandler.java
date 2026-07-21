package com.dducwsjvbe.employee_service.command.event;

import com.dducwsjvbe.employee_service.command.data.Employee;
import com.dducwsjvbe.employee_service.command.data.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmployeeEventsHandler {
    @Autowired
    private EmployeeRepository employeeRepository;

    @EventHandler
    public void on(EmployeeCreatedEvent event) {
        if (employeeRepository.existsById(event.getId())) {
            return;
        }
        Employee employee = new Employee();
        BeanUtils.copyProperties(event, employee);
        employeeRepository.save(employee);
    }

    @EventHandler
    public void on(EmployeeUpdateEvent event) {
        Employee employee = employeeRepository.findById(event.getId()).orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!employee.getFirstName().equals(event.getFirstName())) {
            employee.setFirstName(event.getFirstName());
        }
        if (!employee.getLastName().equals(event.getLastName())) {
            employee.setLastName(event.getLastName());
        }
        if (!employee.getKin().equals(event.getKin())) {
            employee.setKin(event.getKin());
        }
        employee.setIsDisciplined(Boolean.FALSE);
        employeeRepository.save(employee);
    }

    @EventHandler
    public void on(EmployeeDeleteEvent event) {
        employeeRepository.findById(event.getId())
                .ifPresentOrElse(
                        employee -> employeeRepository.delete(employee), // tìm thấy → xóa
                        () -> log.warn("Employee {} not found, skip delete", event.getId()) // không thấy → log warn
                );
    }

}
