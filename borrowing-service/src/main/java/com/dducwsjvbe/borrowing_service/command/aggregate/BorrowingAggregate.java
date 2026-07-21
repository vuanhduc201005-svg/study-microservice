package com.dducwsjvbe.borrowing_service.command.aggregate;

import com.dducwsjvbe.borrowing_service.command.command.CreateBorrowingCommand;
import com.dducwsjvbe.borrowing_service.command.command.DeleteBorrowingCommand;
import com.dducwsjvbe.borrowing_service.command.event.BorrowingCreatedEvent;
import com.dducwsjvbe.borrowing_service.command.event.BorrowingDeleteEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Aggregate
@NoArgsConstructor
public class BorrowingAggregate {
    @AggregateIdentifier
    private String id;

    private String bookId;

    private String employeeId;

    private Date borrowingDate;

    private Date returnDate;

    @CommandHandler
    public BorrowingAggregate(CreateBorrowingCommand createBorrowingCommand) {
        BorrowingCreatedEvent borrowingCreatedEvent = new BorrowingCreatedEvent(
                createBorrowingCommand.getId(),
                createBorrowingCommand.getBookId(),
                createBorrowingCommand.getEmployeeId(),
                createBorrowingCommand.getBorrowingDate()
        );
        AggregateLifecycle.apply(borrowingCreatedEvent);
    }
    @EventSourcingHandler
    public void on(BorrowingCreatedEvent borrowingCreatedEvent) {
        this.id = borrowingCreatedEvent.getId();
        this.bookId = borrowingCreatedEvent.getBookId();
        this.employeeId = borrowingCreatedEvent.getEmployeeId();
        this.borrowingDate = borrowingCreatedEvent.getBorrowingDate();
    }

    @CommandHandler
    public void on(DeleteBorrowingCommand deleteBorrowingCommand) {
        BorrowingDeleteEvent borrowingDeleteEvent = new BorrowingDeleteEvent();
        BeanUtils.copyProperties(deleteBorrowingCommand, borrowingDeleteEvent);
        AggregateLifecycle.apply(borrowingDeleteEvent);
    }

    @EventSourcingHandler
    public void on(BorrowingDeleteEvent borrowingDeleteEvent) {
        AggregateLifecycle.markDeleted();
    }
}
