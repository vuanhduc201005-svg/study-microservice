package com.dducwsjvbe.book_service.command.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateRequest {
private String id;

@NotBlank(message = "Book name is mandatory")
private String name;

@NotBlank(message = "Book author is mandatory")
private String author;

private Boolean isReady;
}
