package com.dducwsjvbe.article_service.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDeleteEvent {
    @TargetAggregateIdentifier
    private String id;

}
