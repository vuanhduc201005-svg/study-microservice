package com.dducwsjvbe.borrowing_service.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteBorrowingCommand {
    @TargetAggregateIdentifier
    private String id;
}
