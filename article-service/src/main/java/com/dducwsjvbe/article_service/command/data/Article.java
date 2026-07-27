package com.dducwsjvbe.article_service.command.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "articles")
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
