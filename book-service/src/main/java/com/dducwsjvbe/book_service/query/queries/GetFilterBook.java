package com.dducwsjvbe.book_service.query.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetFilterBook {
    private int pageNo;
    private int pageSize;
    private String[] book;
    private Boolean isReady;
}
