package com.dducwsjvbe.employee_service.command.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query("""
            SELECT e.id
                  FROM Employee e 
                        WHERE e.isDisciplined =:isDisciplined 
            """)
    Page<String> findAllEmployeesIds(Pageable pageable,@Param("isDisciplined") Boolean isDisciplined);

    @Query("""
                        SELECT e FROM Employee e
            WHERE e.id IN :ids
            """)
    List<Employee> findAllByIds(@Param("ids") List<String> ids);
}