package com.dducwsjvbe.borrowing_service.command.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingCreateRequest {
    private String bookId;
    private String employeeId;
}
