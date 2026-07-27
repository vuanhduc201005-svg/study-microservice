package com.dducwsjvbe.article_service.command.event;

import com.dducwsjvbe.article_service.command.data.Article;
import com.dducwsjvbe.article_service.command.data.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class ArticleEventsHandler {
    private final ArticleRepository articleRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @EventHandler
    public void on(ArticleCreatedEvent event) {
        if (articleRepository.existsById(event.getId())) {
            return;
        }
        Article article = new Article();
        article.setId(event.getId());
        article.setName(event.getName());
        article.setMessage(event.getMessage());
        article.setAuthor(event.getAuthor());
        article.setFileId(event.getFileId());
        article.setViews(event.getViews());
        article.setIsReady(event.getIsReady());
        articleRepository.save(article);
        kafkaTemplate.send("delete-cache-topic", event.getId());
    }

    @EventHandler
    public void on(ArticleUpdateEvent event) {
        Article article = articleRepository.findById(event.getId()).orElseThrow(() -> new RuntimeException("Book not found" + event.getId()));
        if (event.getIsReady() != null) {
            article.setIsReady(Boolean.TRUE);
        }
        articleRepository.save(article);
        kafkaTemplate.send("delete-cache-topic", event.getId());
    }

    @EventHandler
    public void on(ArticleDeleteEvent event) {
        try {
            articleRepository.deleteById(event.getId());
            kafkaTemplate.send("article-delete-topic", event.getId());
            kafkaTemplate.send("delete-cache-topic", event.getId());
        }catch (Exception e) {
            kafkaTemplate.send("article-delete-topic", event.getId());
            kafkaTemplate.send("delete-cache-topic", event.getId());
        }
    }
}
