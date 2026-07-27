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
public class DeleteArticleCommand {
    @TargetAggregateIdentifier
    private String id;
}
