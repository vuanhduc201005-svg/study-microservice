package com.dducwsjvbe.article_service.command.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "articles",indexes = {
        @Index(name = "idx_article_name",columnList = "name"),
        @Index(name = "idx_article_message",columnList = "message"),
        @Index(name = "idx_article_is_ready",columnList = "is_ready"),
        @Index(
                name = "idx_article_name_message_is_ready",
                columnList = "name, message, is_ready"
        )
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "file_id", nullable = false)
    private String fileId;

    @Column(name = "views", nullable = false)
    private Long views;


    @Column(name = "is_ready", nullable = false)
    private Boolean isReady;
}
