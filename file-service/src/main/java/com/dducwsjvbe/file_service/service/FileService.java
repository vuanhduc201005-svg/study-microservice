package com.dducwsjvbe.file_service.service;

import com.dducwsjvbe.file_service.model.response.ResponseData;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    @Transactional
    ResponseData<?> saveChunk(String fileId, String fileName, int chunkIndex, int totalChunks, MultipartFile file, String articleId,String articleMessage,String articleName, String userId) throws IOException,RuntimeException;



}
