package com.dducwsjvbe.article_service.query.service.searchfilter;

import com.dducwsjvbe.article_service.command.data.Article;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    List<Article> findByFilter(Pageable pageable, String[] article, Boolean isReady);
}
