package com.dducwsjvbe.file_service.controller;


import com.dducwsjvbe.file_service.entity.File;
import com.dducwsjvbe.file_service.model.response.ResponseData;
import com.dducwsjvbe.file_service.repository.FileRepository;
import com.dducwsjvbe.file_service.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j(topic = "File-Service/File-Controller")
@Tag(name = "File-Controller")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FileRepository fileRepository;

    @Operation(method = "POST", summary = "upload", description = "upload")
    @PostMapping("/upload-chunk")
    public ResponseData<?> uploadChunk(
            @RequestParam(value = "fileId") String fileId,
            @RequestParam("fileName") String fileName,
            @RequestParam("articleId") String articleId,
            @RequestParam("articleName") String articleName,
            @RequestParam("articleMessage") String articleMessage,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) throws IOException, RuntimeException {
        String userId = jwt.getSubject();
        log.info("Upload request:authorId={},articleName={},articleMessage={},fileId={},chunkIndex/totalChunks={}/{}",userId,articleName,articleMessage,fileId,chunkIndex,totalChunks);
        return fileService.saveChunk(fileId, fileName, chunkIndex, totalChunks, file, articleId, articleMessage, articleName, userId);
    }

    @Operation(method = "GET", summary = "view", description = "view")
    @GetMapping("/path")
    public ResponseEntity<?> view(@RequestParam String fileId, @RequestParam String articleId) {
        log.info("search file path");
        File file = fileRepository.findByFileId(fileId);
        if (file == null) {
            throw new RuntimeException("file not found");
        }
        kafkaTemplate.send("article-up-view-topic", articleId);
        String path = file.getFilePath();

        if (path.startsWith("./") || path.startsWith(".\\")) {
            path = path.substring(1);
        }
        return ResponseEntity.ok(path);
    }


}
/*
@RequestParam("username") String userId,
 */