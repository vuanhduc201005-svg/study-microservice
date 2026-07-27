package com.dducwsjvbe.article_service.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCreatedEvent {
    private String id;
    private String name;
    private String message;
    private String author;
    private String fileId;
    private Long views;
    private Boolean isReady;
}
