package com.dducwsjvbe.book_service.query.service.searchfilter;

import com.dducwsjvbe.book_service.command.data.Book;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    List<Book> findByFilter(Pageable pageable, String[] book, Boolean isReady);
}
