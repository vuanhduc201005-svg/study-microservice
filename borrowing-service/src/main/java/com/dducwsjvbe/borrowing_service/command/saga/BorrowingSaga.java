package com.dducwsjvbe.borrowing_service.command.saga;

import com.dducwsjvbe.borrowing_service.command.command.DeleteBorrowingCommand;
import com.dducwsjvbe.borrowing_service.command.event.BorrowingCreatedEvent;

import com.dducwsjvbe.borrowing_service.command.event.BorrowingDeleteEvent;
import com.dducwsjvbe.common_service.callapi.command.command.RollBackStatusBookCommand;
import com.dducwsjvbe.common_service.callapi.command.command.RollBackStatusBookEvent;
import com.dducwsjvbe.common_service.callapi.command.command.UpdateStatusBookCommand;
import com.dducwsjvbe.common_service.callapi.command.command.UpdateStatusBookEvent;
import com.dducwsjvbe.common_service.callapi.model.items.BookResponse;
import com.dducwsjvbe.common_service.callapi.model.items.EmployeeResponse;
import com.dducwsjvbe.common_service.callapi.query.queries.GetFilterBook;
import com.dducwsjvbe.common_service.callapi.query.queries.GetFilterEmployee;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;

import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Saga
@NoArgsConstructor
@Slf4j(topic = "Borrowing-Saga")
public class BorrowingSaga {
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private QueryGateway queryGateway;

    //    private final WebClient bookServiceClient;
//    private final WebClient employeeServiceClient;
//1
    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    private void handle(BorrowingCreatedEvent borrowingCreatedEvent) {
        log.info("Borrowing-Saga:handle BorrowingCreatedEvent,bookId={},employeeId={}", borrowingCreatedEvent.getBookId(), borrowingCreatedEvent.getEmployeeId());
        try {

            GetFilterBook getFilterBook = new GetFilterBook();
            getFilterBook.setBook(new String[]{"d:" + borrowingCreatedEvent.getBookId()});
            getFilterBook.setPageNo(0);
            getFilterBook.setPageSize(1);
            getFilterBook.setIsReady(true);
            List<BookResponse> listBook = queryGateway.query(getFilterBook, ResponseTypes.multipleInstancesOf(BookResponse.class)).join();

            if (listBook.isEmpty()) {
                throw new RuntimeException("book not found");
            } else if (listBook.get(0).getIsReady() == Boolean.FALSE) {
                throw new RuntimeException("book is not ready");
            } else {//1T
                SagaLifecycle.associateWith("bookId", borrowingCreatedEvent.getBookId());
                UpdateStatusBookCommand updateStatusBookCommand = new UpdateStatusBookCommand(borrowingCreatedEvent.getBookId(), Boolean.FALSE, borrowingCreatedEvent.getEmployeeId(), borrowingCreatedEvent.getId());
                commandGateway.sendAndWait(updateStatusBookCommand);
            }

        } catch (Exception ex) {//1F
            rollBackBorrowingRecord(borrowingCreatedEvent.getId());
            log.error("bookId={},employeeId={},errorMessage={}", borrowingCreatedEvent.getBookId(), borrowingCreatedEvent.getEmployeeId(), ex.getMessage());
        }
    }
//2(1T)
    @SagaEventHandler(associationProperty = "bookId")
    public void handle(UpdateStatusBookEvent updateStatusBookEvent) {
        log.info("Borrowing-Saga:handle UpdateStatusBookCommand,bookId={},employeeId={}", updateStatusBookEvent.getBookId(), updateStatusBookEvent.getEmployeeId());
        try {
            GetFilterEmployee getFilterEmployee = new GetFilterEmployee();
            getFilterEmployee.setEmployee(new String[]{"id:" + updateStatusBookEvent.getEmployeeId()});
            getFilterEmployee.setPageNo(0);
            getFilterEmployee.setPageSize(1);
            getFilterEmployee.setIsDisciplined(true);

            List<EmployeeResponse> listEmployee = queryGateway.query(getFilterEmployee, ResponseTypes.multipleInstancesOf(EmployeeResponse.class)).join();
            if (listEmployee.isEmpty()) {
                throw new RuntimeException("employee not found");
            } else if (listEmployee.get(0).getIsDisciplined() == Boolean.FALSE) {
                throw new RuntimeException("employee is not ready");
            } else {//2T
                log.info("borrowing book successfully");
                SagaLifecycle.end();
            }
        } catch (Exception ex) {//2F
            rollBackBookStatus(updateStatusBookEvent.getBookId(), updateStatusBookEvent.getEmployeeId(), updateStatusBookEvent.getBorrowingId());
            log.error("bookId={},employeeId={},errorMessage={}", updateStatusBookEvent.getBookId(), updateStatusBookEvent.getEmployeeId(), ex.getMessage());
        }

    }
//3(2F)
    @SagaEventHandler(associationProperty = "bookId")
    public void handle(RollBackStatusBookEvent rollBackStatusBookEvent) {
        log.info("Borrowing-Saga:handle RollBackStatusBookCommand,bookId={}", rollBackStatusBookEvent.getBookId());
        rollBackBorrowingRecord(rollBackStatusBookEvent.getBorrowingId());
    }

    @SagaEventHandler(associationProperty = "bookId")
    @EndSaga
    private void handle(BorrowingDeleteEvent borrowingDeleteEvent) {
        log.info("Borrowing-Saga:handle BorrowingDeleteEvent,borrowingId={}", borrowingDeleteEvent.getId());
        SagaLifecycle.end();
    }
//1F 2F
    private void rollBackBorrowingRecord(String id) {
        DeleteBorrowingCommand deleteBorrowingCommand = new DeleteBorrowingCommand(id);
        commandGateway.sendAndWait(deleteBorrowingCommand);
    }
//2F
    private void rollBackBookStatus(String bookId, String employeeId, String borrowingId) {
        RollBackStatusBookCommand rollBackStatusBookCommand = new RollBackStatusBookCommand(bookId, Boolean.TRUE, employeeId, borrowingId);
        commandGateway.sendAndWait(rollBackStatusBookCommand);
    }

}
/* cách service call // 2 api nếu không dùng axon
  Mono<List<BookResponse>> bookMono = bookServiceClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/books")
                            .queryParam("pageNo", 0)
                            .queryParam("pageSize", 1)
                            .queryParam("book", "id:" + borrowingCreatedEvent.getBookId())
                            .queryParam("isReady", true)
                            .build())
                    .retrieve()
                    .bodyToFlux(BookResponse.class)
                    .collectList();

            Mono<List<EmployeeResponse>> employeeMono = employeeServiceClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/employees")
                            .queryParam("pageNo", 0)
                            .queryParam("pageSize", 1)
                            .queryParam("employee","id:" + borrowingCreatedEvent.getEmployeeId())
                            .queryParam("isDisciplined", true)
                            .build())
                    .retrieve()
                    .bodyToFlux(EmployeeResponse.class)
                    .collectList();

            // gọi // +blocking io
            Tuple2<List<BookResponse>, List<EmployeeResponse>> results =
                    Mono.zip(bookMono, employeeMono).block();

            List<BookResponse> books = results.getT1();
            if (books.isEmpty()) {
                throw new RuntimeException("No books found");
            }
            List<EmployeeResponse> employees = results.getT2();
            if (employees.isEmpty()) {
                throw new RuntimeException("No employees found");
            }
 */
/*
get(0) dùng khi biết chắc list chỉ có 1 phần tử
 */