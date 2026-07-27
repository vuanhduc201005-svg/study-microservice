package com.dducwsjvbe.web_ui.service;

import com.dducwsjvbe.web_ui.dto.FileChunkUploadRequest;
import com.dducwsjvbe.web_ui.dto.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "File-Upload-Service-Impl")
public class FileUploadServiceImpl implements FileUploadService {

    private final RestClient restClient;

    @Value("${GatewayPort.url}")
    private String gatewayPort;

    @Override
    public ResponseData<?> uploadChunk(FileChunkUploadRequest request, MultipartFile file) {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("fileId", request.getFileId());
        body.add("fileName", request.getFileName());
        body.add("articleId", request.getArticleId());
        body.add("articleName", request.getArticleName());
        body.add("articleMessage", request.getArticleMessage());
        body.add("chunkIndex", request.getChunkIndex());
        body.add("totalChunks", request.getTotalChunks());
        body.add("file", toMultipartResource(file));

        log.info("Forwarding chunk {}/{} of fileId={} to gateway",
                request.getChunkIndex() + 1, request.getTotalChunks(), request.getFileId());

        // Không cần .header(AUTHORIZATION, ...) nữa
        // -> RestClientConfig đã có requestInterceptor tự lấy token từ TokenContext và gắn vào mọi request
        try {
            com.dducwsjvbe.web_ui.dto.ResponseData<?>rs = restClient.post()
                    .uri(gatewayPort +"/api/v1/files/upload-chunk")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(ResponseData.class);
            log.info("response gateway={}",rs);
            return rs;
        } catch (RestClientResponseException e) {
            log.error("Gateway returned error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Gateway error: " + e.getResponseBodyAsString());
        }catch (Exception e) {
            log.error("Exception in file upload service", e);
            throw new RuntimeException("Exception in file upload service"+e.getMessage());
        }
    }

    // ✅ CÁCH MỚI: Truyền Stream, không tốn RAM, phản hồi tức thì
    private Resource toMultipartResource(MultipartFile file) {
        try {
            return new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename(), file.getSize());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file stream", e);
        }
    }

    public static class MultipartInputStreamFileResource extends InputStreamResource {
        private final String filename;
        private final long contentLength;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename, long contentLength) {
            super(inputStream);
            this.filename = filename;
            this.contentLength = contentLength;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() {
            return this.contentLength;
        }
    }

}
/*
http://file-service:9007
 */