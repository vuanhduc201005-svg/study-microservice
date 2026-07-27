package com.dducwsjvbe.article_service.async;

import com.dducwsjvbe.article_service.command.command.CreateArticleCommand;
import com.dducwsjvbe.article_service.command.command.UpdateArticleCommand;
import com.dducwsjvbe.article_service.command.data.Article;
import com.dducwsjvbe.article_service.command.data.ArticleRepository;
import com.dducwsjvbe.article_service.query.service.ProductSearchCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j(topic = "Async-Service")
@RequiredArgsConstructor
public class AsyncService {
    private final CommandGateway commandGateway;
    private final ArticleRepository articleRepository;
    private final ProductSearchCacheService productSearchCacheService;

    @RetryableTopic(
            attempts = "4", //3 topic retry+1 topic dlq
            backOff = @BackOff(delay = 1000, multiplier = 2), //lần đầu retry sau 1s và nhân 2 lên cho các lần sau
            autoCreateTopics = "true", //từ động retry
            dltStrategy = DltStrategy.FAIL_ON_ERROR, //fail và thêm vào dlq sẽ không retry tiếp
            include = {Exception.class} //chỉ retry khi gặp những lỗi sau
    )
    @KafkaListener(topics = "async-create-article-topic", groupId = "async-create-article-group")
    public void asyncCreateArticle(String message) throws IOException,RuntimeException {

        log.info("async-create-article-topic={}", message);
        String[] arr = message.split(",");
        String fileId = arr[0].substring(arr[0].indexOf('=') + 1);
        String articleId = arr[1].substring(arr[1].indexOf('=') + 1);
        String articleMessage = arr[2].substring(arr[2].indexOf('=') + 1);
        String userId = arr[3].substring(arr[3].indexOf('=') + 1);
        String articleName = arr[4].substring(arr[4].indexOf('=') + 1);
        String filePath = arr[5].substring(arr[5].indexOf('=') + 1);
        validateFileExists(filePath);
        CreateArticleCommand command = new CreateArticleCommand();
        command.setId(articleId);
        command.setName(articleName);
        command.setMessage(articleMessage);
        command.setAuthor(userId);
        command.setFileId(fileId);
        command.setViews(0L);
        command.setIsReady(Boolean.FALSE);
        commandGateway.sendAndWait(command);

    }

    @RetryableTopic(
            attempts = "4", //3 topic retry+1 topic dlq
            backOff = @BackOff(delay = 1000, multiplier = 2), //lần đầu retry sau 1s và nhân 2 lên cho các lần sau
            autoCreateTopics = "true", //từ động retry
            dltStrategy = DltStrategy.FAIL_ON_ERROR, //fail và thêm vào dlq sẽ không retry tiếp
            include = {Exception.class} //chỉ retry khi gặp những lỗi sau
    )
    @KafkaListener(topics = "article-up-view-topic", groupId = "article-up-view-group")
    public void asyncUpViewArticle(String message) throws IOException,RuntimeException {
        log.info("article-up-view-topic={}", message);
        Article article = articleRepository.findById(message).orElseThrow(() -> new RuntimeException("article not found"));
        UpdateArticleCommand command = new UpdateArticleCommand();
        command.setId(article.getId());
        command.setViews(article.getViews() + 1L);
        commandGateway.sendAndWait(command);
    }

    @RetryableTopic(
            attempts = "4", //3 topic retry+1 topic dlq
            backOff = @BackOff(delay = 1000, multiplier = 2), //lần đầu retry sau 1s và nhân 2 lên cho các lần sau
            autoCreateTopics = "true", //từ động retry
            dltStrategy = DltStrategy.FAIL_ON_ERROR, //fail và thêm vào dlq sẽ không retry tiếp
            include = {Exception.class} //chỉ retry khi gặp những lỗi sau
    )
    @KafkaListener(topics = "delete-cache-topic", groupId = "delete-cache-group")
    public void asyncDeleteCache(String message) throws IOException,RuntimeException {
        log.info("delete-cache-topic={}", message);
        productSearchCacheService.evictAll();
    }

    @DltHandler
    void processDltMessage(@Payload String message) {
        log.info("DLT receive message: {}", message);
    }

    private void validateFileExists(String filePath) throws IOException, RuntimeException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File không tồn tại trên ổ cứng: " + filePath);
        }
    }
}
