package com.dducwsjvbe.book_service.query.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private String id;
    private String name;
    private String author;
    private Boolean isReady;
}
