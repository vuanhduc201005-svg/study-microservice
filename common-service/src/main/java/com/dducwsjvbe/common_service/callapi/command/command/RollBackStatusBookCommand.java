package com.dducwsjvbe.common_service.callapi.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RollBackStatusBookCommand {
    @TargetAggregateIdentifier
    private String bookId;

    private Boolean isReady;

    private String employeeId;

    private String borrowingId;

}
