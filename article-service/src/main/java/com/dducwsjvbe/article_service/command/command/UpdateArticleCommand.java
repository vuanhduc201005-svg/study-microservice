package com.dducwsjvbe.article_service.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateArticleCommand {
    @TargetAggregateIdentifier
    private String id;

    private String name;
    private String message;
    private String author;
    private String fileId;
    private Long views;
    private Boolean isReady;
}
