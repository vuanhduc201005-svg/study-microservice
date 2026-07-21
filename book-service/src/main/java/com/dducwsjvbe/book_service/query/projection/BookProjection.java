package com.dducwsjvbe.book_service.query.projection;

import com.dducwsjvbe.book_service.command.data.Book;
import com.dducwsjvbe.book_service.command.data.BookRepository;
import com.dducwsjvbe.book_service.query.model.BookResponse;
import com.dducwsjvbe.book_service.query.queries.GetFilterBook;
import com.dducwsjvbe.book_service.query.service.searchfilter.SearchService;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Component
public class BookProjection {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private SearchService searchService;
    @QueryHandler
    public List<BookResponse> handle(GetFilterBook getFilterBook) {
        Pageable pageable= PageRequest.of(getFilterBook.getPageNo(), getFilterBook.getPageSize());
        if (getFilterBook.getBook()==null || getFilterBook.getBook().length==0 || Arrays.stream(getFilterBook.getBook()).allMatch(StringUtils::isEmpty)) {
            Page<String> listIds = bookRepository.findAllBooksIds(pageable,getFilterBook.getIsReady());
            List<Book>listBook=bookRepository.findAllByIds(listIds.getContent());
            return listBook.stream().map(book -> {
                BookResponse bookResponse = new BookResponse();
                BeanUtils.copyProperties(book, bookResponse);
                return bookResponse;
            }).toList();
        }
        List<Book>books=searchService.findByFilter(pageable,getFilterBook.getBook(),getFilterBook.getIsReady());
        return books.stream().map(book -> {
            BookResponse bookResponse = new BookResponse();
            BeanUtils.copyProperties(book, bookResponse);
            return bookResponse;
        }).toList();
    }
}
