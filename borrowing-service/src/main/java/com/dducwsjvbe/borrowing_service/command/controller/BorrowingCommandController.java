package com.dducwsjvbe.borrowing_service.command.controller;

import com.dducwsjvbe.borrowing_service.command.command.CreateBorrowingCommand;
import com.dducwsjvbe.borrowing_service.command.model.request.BorrowingCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/borrowings")
@Slf4j(topic = "Borrowing-Command-Controller")
public class BorrowingCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String createBorrowing(@RequestBody BorrowingCreateRequest borrowingCreateRequest) {
        log.info("Borrowing-Command-Controller:createBorrowing");
        CreateBorrowingCommand createBorrowingCommand = new CreateBorrowingCommand(
                UUID.randomUUID().toString(),
                borrowingCreateRequest.getBookId(),
                borrowingCreateRequest.getEmployeeId(),
                new Date()
        );
        return commandGateway.sendAndWait(createBorrowingCommand);

    }

}
