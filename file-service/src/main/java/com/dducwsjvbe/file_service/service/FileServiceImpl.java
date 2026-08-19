package com.dducwsjvbe.file_service.service;

import com.dducwsjvbe.file_service.model.response.FileUploadResponse;
import com.dducwsjvbe.file_service.model.response.ResponseData;
import com.dducwsjvbe.file_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j(topic = "File-Service/File-Service-Impl")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.temp.path:./temp}")
    private String tempPath;

    private final FileRepository fileRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Map<String, UploadProgressTracker> uploadProgress = new ConcurrentHashMap<>();

    @Override
    public ResponseData<?> saveChunk(String fileId, String fileName, int chunkIndex, int totalChunks, MultipartFile file, String articleId, String articleMessage, String articleName, String userId) throws IOException, RuntimeException {
        log.info("Upload request:authorId={},articleName={},articleMessage={},fileId={},chunkIndex/totalChunks={}/{}",userId,articleName,articleMessage,fileId,chunkIndex,totalChunks);
        if (!fileId.matches("^[a-zA-Z0-9_-]+$") || !fileName.matches("^[a-zA-Z0-9_-]+\\.[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Invalid file id format: " + fileId);
        }
        //put fileId nếu chưa tồn tại=putIfAbsent tránh việc cả 3 chunk vào 1 lúc và tại ra 3 fileId
        uploadProgress.putIfAbsent(fileId, new UploadProgressTracker(fileId, fileName, totalChunks));
        UploadProgressTracker tracker = uploadProgress.get(fileId);
        // create object ./temp/fileId
        Path tempDir = Paths.get(tempPath, fileId);
        //create file ./temp/fileId
        Files.createDirectories(tempDir);
        // create object temp/fileid/chunk_0123
        Path chunkPath = tempDir.resolve("chunk_" + chunkIndex);
        //ghi dl của chunk(mảnh nhỏ chia ra từ file) và lưu vào cái đg dẫn chunkPath
        Files.write(chunkPath, file.getBytes());
        //đánh dấu chunk đã upload thành công
        tracker.markChunkCompleted(chunkIndex);
        if (!tracker.isComplete()) {
            FileUploadResponse fileUploadResponse = FileUploadResponse.builder()
                    .fileName(fileName)
                    .chunkIndex(chunkIndex)
                    .uploadProgress(tracker.getProgress())
                    .completed(false)
                    .build();
            return new ResponseData<>(
                    HttpStatus.OK.value(),
                    "Chunk " + (chunkIndex + 1) + " uploaded successfully",
                    fileUploadResponse
            );
        }
        //khi nhận đủ chunk thì dùng kafka để ghép,save db,delete temp
        String message = String.format("fileId=%s,fileName=%s,articleId=%s,articleMessage=%s,userId=%s,articleName=%s", fileId, fileName, articleId, articleMessage, userId, articleName);
        kafkaTemplate.send("async-upload-topic", message);
        // Xóa tracker
        uploadProgress.remove(fileId);
        FileUploadResponse finalResponse = FileUploadResponse.builder()
                .fileName(fileName)
                .chunkIndex(chunkIndex)
                .uploadProgress(100.0)
                .completed(true)
                .build();
        return new ResponseData<>(
                HttpStatus.CREATED.value(),
                "File uploaded successfully",
                finalResponse);
    }

    private static class UploadProgressTracker {
        private final String fileId;
        private final String fileName;
        private final int totalChunks;
        private final boolean[] completedChunks;

        public UploadProgressTracker(String fileId, String fileName, int totalChunks) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.totalChunks = totalChunks;
             /*
             vd totalChunk=3
             =>3=boolean[0,1,2]
             * */
            this.completedChunks = new boolean[totalChunks];
        }

        public void markChunkCompleted(int index) {
            if (index < 0 || index >= totalChunks) {
                log.error("invalid chunk index:{},totalChunks:{}", index, totalChunks - 1);
                throw new IllegalArgumentException("invalid chunk index:" + index + "totalChunks:" + (totalChunks - 1));
            }
            //index=0 =>boolean[true false false]
            completedChunks[index] = true;
        }

        //isComplete nếu ko if mặc định là true
        public boolean isComplete() {
            for (boolean completed : completedChunks) {
                //cứ còn false là false
                if (!completed) return false;
            }
            return true;
        }

        public double getProgress() {
            int completed = 0;
            for (boolean c : completedChunks) {
                if (c) completed++;
            }
            return (double) completed / totalChunks * 100;
        }
    }

}