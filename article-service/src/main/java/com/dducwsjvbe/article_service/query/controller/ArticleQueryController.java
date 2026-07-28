package com.dducwsjvbe.article_service.query.controller;

import com.dducwsjvbe.article_service.query.model.ArticleResponse;
import com.dducwsjvbe.article_service.query.queries.GetFilterArticle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
@Slf4j(topic = "Article-Query-Controller")
@Tag(name = "Article Query Controller", description = "API for querying article data")
public class ArticleQueryController {
    @Autowired
    private QueryGateway queryGateway;

    @Operation(method = "GET", summary = "search filter admin", description = "search filter admin")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ArticleResponse> queryFilterBookAdmin(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                 @RequestParam(defaultValue = "10", required = false) int pageSize,
                                                 @RequestParam(required = false) String[] article,
                                                 @RequestParam Boolean isReady) {
        log.info("searchFilterArticle");
        GetFilterArticle getFilterArticle = new GetFilterArticle(pageNo, pageSize, article, Boolean.FALSE);
        //lấy kq dạng bất đồng bộ nhưng cần trả kq ngay nên .join để đợi
        List<ArticleResponse> result = queryGateway.query(getFilterArticle, ResponseTypes.multipleInstancesOf(ArticleResponse.class)).join();
        return result;
    }

    @Operation(method = "GET", summary = "search filter", description = "search filter")
    @GetMapping("/true")
    public List<ArticleResponse> queryFilterBook(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                 @RequestParam(defaultValue = "10", required = false) int pageSize,
                                                 @RequestParam(required = false) String[] article,
                                                 @RequestParam Boolean isReady) {
        log.info("searchFilterArticle");
        GetFilterArticle getFilterArticle = new GetFilterArticle(pageNo, pageSize, article, Boolean.TRUE);
        //lấy kq dạng bất đồng bộ nhưng cần trả kq ngay nên .join để đợi
        List<ArticleResponse> result = queryGateway.query(getFilterArticle, ResponseTypes.multipleInstancesOf(ArticleResponse.class)).join();
        return result;
    }
}
