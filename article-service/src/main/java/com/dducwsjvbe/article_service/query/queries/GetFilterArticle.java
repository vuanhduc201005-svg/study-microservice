package com.dducwsjvbe.article_service.query.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetFilterArticle {
    private int pageNo;
    private int pageSize;
    private String[] article;
    private Boolean isReady;
}
