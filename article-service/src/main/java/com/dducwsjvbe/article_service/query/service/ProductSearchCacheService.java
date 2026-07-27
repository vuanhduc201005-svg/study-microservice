package com.dducwsjvbe.article_service.query.service;

import com.dducwsjvbe.article_service.query.model.ArticleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "Product-Search-Cache-Service")
@Service
@RequiredArgsConstructor
public class ProductSearchCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_PREFIX = "search_product";
    private static final Duration TTL = Duration.ofMinutes(10);
    private static final Pattern PATTERN =
            Pattern.compile("(\\w+?)([:<>~!])(\\p{Punct}?)(.*?)(\\p{Punct}?)$");


    // =========================================
    // GET
    // =========================================
    public List<ArticleResponse> get(Pageable pageable, String[] article, Boolean isReady) {
        String key = buildKey(pageable, article, isReady);
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("[CACHE HIT] key={}", key);
                return (List<ArticleResponse>) cached;
            }
            log.info("[CACHE MISS] key={}", key);
        } catch (Exception e) {
            log.warn("[CACHE ERROR] get failed key={} error={}", key, e);
        }
        return null;
    }

    // =========================================
    // SET
    // =========================================
    public void set(Pageable pageable, String[] article, Boolean isReady, List<ArticleResponse> data) {
        String key = buildKey(pageable, article, isReady);
        try {
            redisTemplate.opsForValue().set(key, data, TTL);
            log.info("[CACHE SET] key={} ttl={}m", key, TTL.toMinutes());
        } catch (Exception e) {
            log.warn("[CACHE ERROR] set failed key={} error={}", key, e);
        }
    }
    // =========================================
    // BUILD KEY
    // =========================================
    private String buildKey(Pageable pageable, String[] article, Boolean isReady) {
        StringBuilder key = new StringBuilder(CACHE_PREFIX);

        key.append(":page=").append(pageable.getPageNumber());
        key.append(":size=").append(pageable.getPageSize());

        if (isReady != null) {
            key.append(":isReady=").append(isReady);
        }

        if (article != null && article.length > 0) {
            List<String> articleParts = new ArrayList<>();
            for (String p : article) {
                Matcher matcher = PATTERN.matcher(p);
                if (matcher.find()) {
                    articleParts.add(matcher.group(1) + matcher.group(2) + matcher.group(4));
                }
            }
            Collections.sort(articleParts);
            key.append(":articles=[")
                    .append(String.join(",", articleParts))
                    .append("]");
        }


        return key.toString();
    }

    // =========================================
    // EVICT:xóa khi post,up,del
    // =========================================
    public void evictByStatus(Boolean isReady) {
        evictByPattern(CACHE_PREFIX + ":*:isReady=" + isReady + "*");
    }

    public void evictAll() {
        evictByPattern(CACHE_PREFIX + ":*");
    }

    private void evictByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("[CACHE EVICT] pattern={} count={}", pattern, keys.size());
            }
        } catch (Exception e) {
            log.warn("[CACHE ERROR] evict failed pattern={} error={}", pattern, e.getMessage());
        }
    }
}
