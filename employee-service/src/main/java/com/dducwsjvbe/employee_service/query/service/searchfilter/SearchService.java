package com.dducwsjvbe.employee_service.query.service.searchfilter;

import com.dducwsjvbe.employee_service.command.data.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SearchService {
    List<Employee> findByFilter(Pageable pageable, String[] employee, Boolean isDisciplined);
}
