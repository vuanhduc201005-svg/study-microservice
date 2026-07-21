package com.dducwsjvbe.book_service.command.controller;

import com.dducwsjvbe.book_service.command.command.CreateBookCommand;
import com.dducwsjvbe.book_service.command.command.DeleteBookCommand;
import com.dducwsjvbe.book_service.command.command.UpdateBookCommand;
import com.dducwsjvbe.book_service.command.model.BookCreateRequest;
import com.dducwsjvbe.book_service.command.model.BookUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
@Slf4j(topic = "Book-Command-Controller")
@Tag(name = "Book Command Controller", description = "API for managing book data")
public class BookCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @Operation(method = "POST", summary = "create book", description = "create book")
    @PostMapping
    public String addBook(@Valid @RequestBody BookCreateRequest bookRequest) {
        log.info("addBook");
        CreateBookCommand createBookCommand = new CreateBookCommand(
                UUID.randomUUID().toString(),
                bookRequest.getName(),
                bookRequest.getAuthor(),
                bookRequest.getIsReady());
        return commandGateway.sendAndWait(createBookCommand);
    }

    @Operation(method = "PUT",summary = "update book", description = "update book")
    @PutMapping("/{bookId}")
    public String updateBook(@PathVariable String bookId,
                              @RequestBody BookUpdateRequest bookRequest) {
        log.info("updateBook");
        UpdateBookCommand updateBookCommand = new UpdateBookCommand(
                bookId,
                bookRequest.getName(),
                bookRequest.getAuthor(),
                bookRequest.getIsReady());
        return commandGateway.sendAndWait(updateBookCommand);
    }

    @Operation(method = "DELETE",summary = "delete book", description = "delete book")
    @DeleteMapping("/{bookId}")
    public String deleteBook(@PathVariable String bookId) {
        log.info("deleteBook");
        DeleteBookCommand deleteBookCommand = new DeleteBookCommand(
                bookId);
        return commandGateway.sendAndWait(deleteBookCommand);
    }

}
