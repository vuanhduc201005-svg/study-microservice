package com.dducwsjvbe.book_service.command.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookUpdateRequest {
    private String name;
    private String author;
    private Boolean isReady;
}
