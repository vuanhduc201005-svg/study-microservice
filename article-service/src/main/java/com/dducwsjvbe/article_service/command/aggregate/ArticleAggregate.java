package com.dducwsjvbe.article_service.command.aggregate;


import com.dducwsjvbe.article_service.command.command.CreateArticleCommand;
import com.dducwsjvbe.article_service.command.command.DeleteArticleCommand;
import com.dducwsjvbe.article_service.command.command.UpdateArticleCommand;
import com.dducwsjvbe.article_service.command.event.ArticleCreatedEvent;

import com.dducwsjvbe.article_service.command.event.ArticleDeleteEvent;
import com.dducwsjvbe.article_service.command.event.ArticleUpdateEvent;
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
public class ArticleAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private String message;
    private String author;
    private String fileId;
    private Long views;
    private Boolean isReady;

    @CommandHandler
    public ArticleAggregate(CreateArticleCommand command) {
        ArticleCreatedEvent articleCreatedEvent = new ArticleCreatedEvent();
        BeanUtils.copyProperties(command, articleCreatedEvent);
        AggregateLifecycle.apply(articleCreatedEvent
        );
    }

    @EventSourcingHandler
    public void on(ArticleCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.message = event.getMessage();
        this.author = event.getAuthor();
        this.fileId = event.getFileId();
        this.views = event.getViews();
        this.isReady = event.getIsReady();
    }

    @CommandHandler
    public void handle(UpdateArticleCommand command) {
        boolean hasAnyField = StringUtils.hasText(String.valueOf(command.getIsReady()));
        // Không có gì thay đổi → không apply event, trả về luôn
        if (!hasAnyField) {
            return;
        }
        // So sánh với state hiện tại của Aggregate
        boolean isChanged =
                (StringUtils.hasText(String.valueOf(command.getIsReady())) && !command.getIsReady().equals(this.isReady));

        // Không có gì thay đổi thật sự → không apply event
        if (!isChanged) {
            return;
        }
        ArticleUpdateEvent articleUpdateEvent = new ArticleUpdateEvent(
                command.getId(),
                this.name,
                this.message,
                this.author,
                this.fileId,
                this.views,
                Boolean.FALSE
        );
        AggregateLifecycle.apply(articleUpdateEvent);
    }

    @EventSourcingHandler
    public void on(ArticleUpdateEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.author = event.getAuthor();
        this.fileId = event.getFileId();
        this.views = event.getViews();
        this.isReady = event.getIsReady();
    }

    @CommandHandler
    public void handle(DeleteArticleCommand command) {
        ArticleDeleteEvent articleDeleteEvent = new ArticleDeleteEvent();
        BeanUtils.copyProperties(command, articleDeleteEvent);
        AggregateLifecycle.apply(articleDeleteEvent);
    }

    @EventSourcingHandler
    public void on(ArticleDeleteEvent event) {
        AggregateLifecycle.markDeleted();
    }
//
//    @CommandHandler
//    public void handle(UpdateStatusBookCommand command) {
//        UpdateStatusBookEvent updateStatusBookEvent = new UpdateStatusBookEvent();
//        BeanUtils.copyProperties(command, updateStatusBookEvent);
//        AggregateLifecycle.apply(updateStatusBookEvent);
//    }
//
//    @EventSourcingHandler
//    public void on(UpdateStatusBookEvent event) {
//        this.id = event.getBookId();
//        this.isReady = event.getIsReady();
//    }
//
//    @CommandHandler
//    public void handle(RollBackStatusBookCommand command) {
//        RollBackStatusBookEvent rollBackStatusBookEvent = new RollBackStatusBookEvent();
//        BeanUtils.copyProperties(command, rollBackStatusBookEvent);
//        AggregateLifecycle.apply(rollBackStatusBookEvent);
//    }
//
//    @EventSourcingHandler
//    public void on(RollBackStatusBookEvent event) {
//        this.id = event.getBookId();
//        this.isReady = event.getIsReady();
//    }
}
/*
commandGateway.sendAndWait(new CreateArticleCommand(...))
→ Axon tìm @CommandHandler nhận kiểu CreateArticleCommand
→ Chỉ có constructor nhận CreateArticleCommand ✅
và body gửi đi là chìa khóa để axon biết nhảy vào @CommandHandler nào
=> mỗi @CommandHandler chỉ nhận 1 kiểu command duy nhất
 */