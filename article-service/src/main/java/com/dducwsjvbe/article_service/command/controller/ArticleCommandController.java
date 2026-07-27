package com.dducwsjvbe.article_service.command.controller;

import com.dducwsjvbe.article_service.command.command.CreateArticleCommand;
import com.dducwsjvbe.article_service.command.command.DeleteArticleCommand;
import com.dducwsjvbe.article_service.command.command.UpdateArticleCommand;
import com.dducwsjvbe.article_service.command.model.ArticleCreateRequest;
import com.dducwsjvbe.article_service.command.model.ArticleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/articles")
@Slf4j(topic = "Article-Command-Controller")
public class ArticleCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @Operation(method = "POST", summary = "create article", description = "create article")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String addArticle(@Valid @RequestBody ArticleCreateRequest articleCreateRequest) {
        log.info("addBook");
        CreateArticleCommand createArticleCommand = new CreateArticleCommand(
                UUID.randomUUID().toString(),
                articleCreateRequest.getName(),
                articleCreateRequest.getMessage(),
                articleCreateRequest.getAuthor(),
                articleCreateRequest.getFileId(),
                0L,
                Boolean.FALSE);
        return commandGateway.sendAndWait(createArticleCommand);
    }

    @Operation(method = "PUT",summary = "update article", description = "update article")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{articleId}")
    public String updateArticle(@PathVariable String articleId,
                             @RequestBody ArticleUpdateRequest articleUpdateRequest) {
        log.info("updateArticle");
        UpdateArticleCommand updateArticleCommand = new UpdateArticleCommand(
                articleId,
                null,
                null,
                null,
                null,
                null,
                Boolean.TRUE);
        return commandGateway.sendAndWait(updateArticleCommand);
    }

    @Operation(method = "DELETE",summary = "delete article", description = "delete article")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{articleId}")
    public String deleteBook(@PathVariable String articleId) {
        log.info("deleteArticle");
        DeleteArticleCommand deleteArticleCommand = new DeleteArticleCommand(
                articleId);
        return commandGateway.sendAndWait(deleteArticleCommand);
    }
}
