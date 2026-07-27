package com.dducwsjvbe.article_service.command.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCreateRequest {
    private String id;

    @NotBlank(message = "Article name is mandatory")
    private String name;

    @NotBlank(message = "Article message is mandatory")
    private String message;

    @NotBlank(message = "Article author is mandatory")
    private String author;

    @NotBlank(message = "Article path is mandatory")
    private String fileId;

    private Long views;
    private Boolean isReady;

}
