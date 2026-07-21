package com.dducwsjvbe.employee_service.query.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetFilterEmployee {
    private int pageNo;
    private int pageSize;
    private String[] employee;
    private Boolean isDisciplined;
}
