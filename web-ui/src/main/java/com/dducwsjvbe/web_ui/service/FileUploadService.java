package com.dducwsjvbe.web_ui.service;

import com.dducwsjvbe.web_ui.dto.FileChunkUploadRequest;
import com.dducwsjvbe.web_ui.dto.ResponseData;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    ResponseData<?> uploadChunk(FileChunkUploadRequest request, MultipartFile file);
}
