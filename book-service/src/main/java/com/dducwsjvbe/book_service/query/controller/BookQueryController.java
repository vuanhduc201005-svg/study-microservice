package com.dducwsjvbe.book_service.query.controller;

import com.dducwsjvbe.book_service.query.model.BookResponse;
import com.dducwsjvbe.book_service.query.queries.GetFilterBook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@Slf4j(topic = "Book-Query-Controller")
@Tag(name = "Book Query Controller", description = "API for querying book data")
public class BookQueryController {
    @Autowired
    private QueryGateway queryGateway;

    @Operation(method = "GET", summary = "search filter", description = "search filter")
    @GetMapping
    public List<BookResponse> queryFilterBook(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                              @RequestParam(defaultValue = "10", required = false) int pageSize,
                                              @RequestParam(required = false) String[] book,
                                              @RequestParam Boolean isReady) {
        log.info("searchFilterBook");
        GetFilterBook getFilterBook = new GetFilterBook();
        //lấy kq dạng bất đồng bộ nhưng cần trả kq ngay nên .join để đợi
        List<BookResponse> result = queryGateway.query(getFilterBook, ResponseTypes.multipleInstancesOf(BookResponse.class)).join();
        return result;
    }
}
