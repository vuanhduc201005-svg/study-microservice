package com.dducwsjvbe.book_service.command.event;

import com.dducwsjvbe.book_service.command.data.Book;
import com.dducwsjvbe.book_service.command.data.BookRepository;
import com.dducwsjvbe.common_service.callapi.command.command.RollBackStatusBookEvent;
import com.dducwsjvbe.common_service.callapi.command.command.UpdateStatusBookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookEventsHandler {
    private final BookRepository bookRepository;

    @EventHandler
    public void on(BookCreatedEvent event) {
        if (bookRepository.existsById(event.getId())) {
            return;
        }
        Book book = new Book();
        BeanUtils.copyProperties(event, book);
        bookRepository.save(book);
    }

    @EventHandler
    public void on(BookUpdateEvent event) {
        Book book = bookRepository.findById(event.getId()).orElseThrow(() -> new RuntimeException("Book not found" + event.getId()));
        if (!book.getName().equals(event.getName()) && StringUtils.hasText(event.getName())) {
            book.setName(event.getName());
        }
        if (!book.getAuthor().equals(event.getAuthor()) && StringUtils.hasText(event.getAuthor())) {
            book.setAuthor(event.getAuthor());
        }
        if (event.getIsReady() != null) {
            book.setIsReady(Boolean.FALSE);
        }
        bookRepository.save(book);
    }

    @EventHandler
    public void on(BookDeleteEvent event) {
        bookRepository.findById(event.getId())
                .ifPresentOrElse(
                        employee -> bookRepository.delete(employee), // tìm thấy → xóa
                        () -> log.warn("Book {} not found, skip delete", event.getId()) // không thấy → log warn
                );
    }

    @EventHandler
    public void on(UpdateStatusBookEvent event) {
        bookRepository.findById(event.getBookId())
                .ifPresentOrElse(
                        book -> {
                            book.setIsReady(event.getIsReady());
                            bookRepository.save(book);
                        },
                        () -> log.warn("Book {} not found, skip update status", event.getBookId())
                );
    }

    @EventHandler
    public void on(RollBackStatusBookEvent event) {
        bookRepository.findById(event.getBookId()).ifPresentOrElse(
                book ->  {
                    book.setIsReady(event.getIsReady());
                    bookRepository.save(book);
                },
                () -> log.warn("Book {} not found, skip roll back", event.getBookId())
        );
    }

}
/*
AggregateLifecycle.apply(new BookCreatedEvent())
→ Axon Server nhận BookCreatedEvent
→ Tìm tất cả @EventHandler nhận kiểu BookCreatedEvent
→ Gọi hết tất cả các handler đó
=> kiểu event là thứ quyết định @EventHandler nào nhận event mà xử lý
 */