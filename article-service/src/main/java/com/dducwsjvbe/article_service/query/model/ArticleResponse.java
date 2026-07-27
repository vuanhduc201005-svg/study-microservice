package com.dducwsjvbe.article_service.query.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponse {
    private String id;
    private String name;
    private String message;
    private String author;
    private String fileId;
    private Long views;
    private Boolean isReady;
}
