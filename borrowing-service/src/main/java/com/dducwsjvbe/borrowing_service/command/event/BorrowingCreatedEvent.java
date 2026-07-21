package com.dducwsjvbe.borrowing_service.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingCreatedEvent {

    private String id;

    private String bookId;

    private String employeeId;

    private Date borrowingDate;
}
