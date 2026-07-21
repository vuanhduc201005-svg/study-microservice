package com.dducwsjvbe.borrowing_service.command.command;

import com.dducwsjvbe.borrowing_service.command.data.Borrowing;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBorrowingCommand {
    @TargetAggregateIdentifier
    private String id;

    private String bookId;

    private String employeeId;

    private Date borrowingDate;

}
