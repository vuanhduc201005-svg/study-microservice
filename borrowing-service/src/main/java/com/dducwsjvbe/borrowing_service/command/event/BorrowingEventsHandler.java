package com.dducwsjvbe.borrowing_service.command.event;

import com.dducwsjvbe.borrowing_service.command.data.Borrowing;
import com.dducwsjvbe.borrowing_service.command.data.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BorrowingEventsHandler {
    private final BorrowingRepository borrowingRepository;

    @EventHandler
    public void on(BorrowingCreatedEvent borrowingCreatedEvent) {
        if (borrowingRepository.existsById(borrowingCreatedEvent.getId())) {
            return;
        }
        Borrowing borrowing = new Borrowing();
        borrowing.setId(borrowingCreatedEvent.getId());
        borrowing.setBookId(borrowingCreatedEvent.getBookId());
        borrowing.setEmployeeId(borrowingCreatedEvent.getEmployeeId());
        borrowing.setBorrowingDate(borrowingCreatedEvent.getBorrowingDate());
        borrowingRepository.save(borrowing);
    }

    @EventHandler
    public void on(BorrowingDeleteEvent borrowingDeleteEvent) {
        if (borrowingRepository.existsById(borrowingDeleteEvent.getId())) {
            borrowingRepository.deleteById(borrowingDeleteEvent.getId());
        }
    }
}
