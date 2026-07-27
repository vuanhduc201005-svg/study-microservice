package com.dducwsjvbe.web_ui.controller;

import com.dducwsjvbe.web_ui.dto.FileChunkUploadRequest;
import com.dducwsjvbe.web_ui.dto.ResponseData;
import com.dducwsjvbe.web_ui.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/web/files")
@RequiredArgsConstructor
@Slf4j(topic = "File-Web-Controller")
public class FileWebController {

    private final FileUploadService fileUploadService;

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/upload-chunk")
    @ResponseBody
    public ResponseEntity<?> uploadChunk(
            @ModelAttribute @Valid FileChunkUploadRequest request,
            @RequestParam("file") MultipartFile file
    ) {
        log.info("Received chunk {}/{} for fileId={}",
                request.getChunkIndex() + 1, request.getTotalChunks(), request.getFileId());

        ResponseData<?> response = fileUploadService.uploadChunk(request, file);
        return ResponseEntity.ok(response);
    }
}
