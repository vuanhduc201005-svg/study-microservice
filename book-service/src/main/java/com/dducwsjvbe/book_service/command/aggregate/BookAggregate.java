package com.dducwsjvbe.book_service.command.aggregate;

import com.dducwsjvbe.book_service.command.command.CreateBookCommand;
import com.dducwsjvbe.book_service.command.command.DeleteBookCommand;
import com.dducwsjvbe.book_service.command.command.UpdateBookCommand;
import com.dducwsjvbe.book_service.command.event.BookCreatedEvent;
import com.dducwsjvbe.book_service.command.event.BookDeleteEvent;
import com.dducwsjvbe.book_service.command.event.BookUpdateEvent;
import com.dducwsjvbe.common_service.callapi.command.command.RollBackStatusBookCommand;
import com.dducwsjvbe.common_service.callapi.command.command.RollBackStatusBookEvent;
import com.dducwsjvbe.common_service.callapi.command.command.UpdateStatusBookEvent;
import com.dducwsjvbe.common_service.callapi.command.command.UpdateStatusBookCommand;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

@Aggregate
@NoArgsConstructor
public class BookAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String author;
    private Boolean isReady;

    @CommandHandler
    public BookAggregate(CreateBookCommand command) {
        BookCreatedEvent bookCreatedEvent = new BookCreatedEvent();
        BeanUtils.copyProperties(command, bookCreatedEvent);
        AggregateLifecycle.apply(bookCreatedEvent);
    }

    @EventSourcingHandler
    public void on(BookCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
    }

    @CommandHandler
    public void handle(UpdateBookCommand command) {
        boolean hasAnyField = StringUtils.hasText(command.getName())
                || StringUtils.hasText(command.getAuthor());
        // Không có gì thay đổi → không apply event, trả về luôn
        if (!hasAnyField) {
            return;
        }
        // So sánh với state hiện tại của Aggregate
        boolean isChanged =
                (StringUtils.hasText(command.getName()) && !command.getName().equals(this.name))
                        || (StringUtils.hasText(command.getAuthor()) && !command.getAuthor().equals(this.author));

        // Không có gì thay đổi thật sự → không apply event
        if (!isChanged) {
            return;
        }
        BookUpdateEvent bookUpdateEvent = new BookUpdateEvent(
                command.getId(),
                StringUtils.hasText(command.getName()) ? command.getName() : this.name,
                StringUtils.hasText(command.getAuthor()) ? command.getAuthor() : this.author,
                Boolean.FALSE
        );
        AggregateLifecycle.apply(bookUpdateEvent);
    }

    @EventSourcingHandler
    public void on(BookUpdateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.isReady = event.getIsReady();
    }

    @CommandHandler
    public void handle(DeleteBookCommand command) {
        BookDeleteEvent bookDeleteEvent = new BookDeleteEvent();
        BeanUtils.copyProperties(command, bookDeleteEvent);
        AggregateLifecycle.apply(bookDeleteEvent);
    }

    @EventSourcingHandler
    public void on(BookDeleteEvent event) {
        AggregateLifecycle.markDeleted();
//        this.id = event.getId();
    }

    @CommandHandler
    public void handle(UpdateStatusBookCommand command) {
        UpdateStatusBookEvent updateStatusBookEvent = new UpdateStatusBookEvent();
        BeanUtils.copyProperties(command, updateStatusBookEvent);
        AggregateLifecycle.apply(updateStatusBookEvent);
    }

    @EventSourcingHandler
    public void on(UpdateStatusBookEvent event) {
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }

    @CommandHandler
    public void handle(RollBackStatusBookCommand command) {
        RollBackStatusBookEvent rollBackStatusBookEvent = new RollBackStatusBookEvent();
        BeanUtils.copyProperties(command, rollBackStatusBookEvent);
        AggregateLifecycle.apply(rollBackStatusBookEvent);
    }

    @EventSourcingHandler
    public void on(RollBackStatusBookEvent event) {
        this.id = event.getBookId();
        this.isReady = event.getIsReady();
    }
}
/*
commandGateway.sendAndWait(new CreateBookCommand(...))
→ Axon tìm @CommandHandler nhận kiểu CreateBookCommand
→ Chỉ có constructor nhận CreateBookCommand ✅
và body gửi đi là chìa khóa để axon biết nhảy vào @CommandHandler nào
=> mỗi @CommandHandler chỉ nhận 1 kiểu command duy nhất
 */