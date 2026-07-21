package com.dducwsjvbe.common_service.callapi.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RollBackStatusBookEvent {
    private String bookId;

    private Boolean isReady;

    private String employeeId;

    private String borrowingId;
}
