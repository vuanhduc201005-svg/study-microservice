package com.dducwsjvbe.article_service.query.projection;

import com.dducwsjvbe.article_service.command.data.Article;
import com.dducwsjvbe.article_service.command.data.ArticleRepository;
import com.dducwsjvbe.article_service.query.model.ArticleResponse;
import com.dducwsjvbe.article_service.query.queries.GetFilterArticle;
import com.dducwsjvbe.article_service.query.service.ProductSearchCacheService;
import com.dducwsjvbe.article_service.query.service.searchfilter.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleProjection {

    private final ArticleRepository articleRepository;
    private final SearchService searchService;
    private final ProductSearchCacheService cacheService;

    @QueryHandler
    public List<ArticleResponse> handle(GetFilterArticle getFilterArticle) {
        Pageable pageable = PageRequest.of(getFilterArticle.getPageNo(), getFilterArticle.getPageSize());
        // CACHE GET
        List<ArticleResponse> cached =
                cacheService.get(pageable, getFilterArticle.getArticle(), getFilterArticle.getIsReady());
        if (cached != null) {
            log.info("cache successfully");
            return cached;
        }
        List<ArticleResponse> fullDataArticle;
        if (getFilterArticle.getArticle() == null || getFilterArticle.getArticle().length == 0 || Arrays.stream(getFilterArticle.getArticle()).allMatch(StringUtils::isEmpty)) {
            Page<String> listIds = articleRepository.findAllArticleIds(pageable, getFilterArticle.getIsReady());
            List<Article> listArticle = articleRepository.findAllByIds(listIds.getContent());
            fullDataArticle = listArticle.stream().map(article -> {
                ArticleResponse articleResponse = new ArticleResponse();
                BeanUtils.copyProperties(article, articleResponse);
                return articleResponse;
            }).collect(Collectors.toList());
            // CACHE SET
            cacheService.set(
                    pageable,
                    getFilterArticle.getArticle(),
                    getFilterArticle.getIsReady(),
                    fullDataArticle
            );
            return fullDataArticle;
        }
        List<Article> articles = searchService.findByFilter(pageable, getFilterArticle.getArticle(), getFilterArticle.getIsReady());

        fullDataArticle = articles.stream().map(article -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(article, articleResponse);
            return articleResponse;
        }).collect(Collectors.toList());
        // CACHE SET
        cacheService.set(
                pageable,
                getFilterArticle.getArticle(),
                getFilterArticle.getIsReady(),
                fullDataArticle
        );
        return fullDataArticle;
    }
}
