package com.dducwsjvbe.file_service.service;


import com.dducwsjvbe.file_service.entity.File;
import com.dducwsjvbe.file_service.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;


@Service
@Slf4j(topic = "File-Service/AsyncKafka-Service")
@RequiredArgsConstructor
public class AsyncKafkaService {
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.temp.path:./temp}")
    private String tempPath;

    private final FileRepository fileRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @RetryableTopic(
            attempts = "4", //3 topic retry+1 topic dlq
            backOff = @BackOff(delay = 1000, multiplier = 2), //lần đầu retry sau 1s và nhân 2 lên cho các lần sau
            autoCreateTopics = "true", //từ động retry
            dltStrategy = DltStrategy.FAIL_ON_ERROR, //fail và thêm vào dlq sẽ không retry tiếp
            include = {Exception.class} //chỉ retry khi gặp những lỗi sau
    )
    @KafkaListener(topics = "article-delete-topic", groupId = "article-delete-group", concurrency = "3")
    public void asyncDeleteArticle(String message) throws IOException, RuntimeException {
        log.info("asyncDeleteArticle,message:{}", message);
        File file = fileRepository.findByArticleId(message);
        if (file == null) {
            throw new RuntimeException("file of article id " + message + " not found");
        }
        try {
            Path filePaths = Paths.get(file.getFilePath());
            Files.deleteIfExists(filePaths);
            fileRepository.deleteById(file.getId());
        } catch (Exception e) {
            log.info("error while deleting file of article id " + message, e);
        }
    }

    @RetryableTopic(
            attempts = "4", //3 topic retry+1 topic dlq
            backOff = @BackOff(delay = 1000, multiplier = 2), //lần đầu retry sau 1s và nhân 2 lên cho các lần sau
            autoCreateTopics = "true", //từ động retry
            dltStrategy = DltStrategy.FAIL_ON_ERROR, //fail và thêm vào dlq sẽ không retry tiếp
            include = {RuntimeException.class} //chỉ retry khi gặp những lỗi sau
    )
    @KafkaListener(topics = "async-upload-topic", groupId = "async-upload-group", concurrency = "3")
    public void asyncUpload(String message) throws IOException, RuntimeException {
        log.info("async-upload-message={}", message);
        String[] arr = message.split(",");
        String fileId = arr[0].substring(arr[0].indexOf('=') + 1);
        String fileName = arr[1].substring(arr[1].indexOf('=') + 1);
        String articleId = arr[2].substring(arr[2].indexOf('=') + 1);
        String articleMessage = arr[3].substring(arr[3].indexOf('=') + 1);
        String userId = arr[4].substring(arr[4].indexOf('=') + 1);
        String articleName = arr[5].substring(arr[5].indexOf('=') + 1);
        log.info("async-upload-message:authorId={},articleName={},articleMessage={},fileId={}",userId,articleName,articleMessage,fileId);
        String finalPath = mergeChunks(fileId, fileName);
        long fileSize = getFileSize(finalPath);

        // Lưu vào database
        File uploadedFiles = File.builder()
                .fileId(fileId)
                .fileName(fileName)
                .filePath(finalPath)
                .fileSize(fileSize)
                .articleId(articleId)
                .isReady(Boolean.FALSE)
                .build();
        fileRepository.save(uploadedFiles);
        log.info("File uploaded and saved to DB: {}", fileName);
        String createArticleMessage = String.format("fileId=%s,articleId=%s,articleMessage=%s,userId=%s,articleName=%s,filePath=%s", fileId, articleId, articleMessage, userId, articleName, finalPath);
        kafkaTemplate.send("async-create-article-topic", createArticleMessage);
        Path tempDir = Paths.get(tempPath, fileId);
        deleteDirectory(tempDir);
    }

    @DltHandler
    void processDltMessage(@Payload String message) {
        log.info("DLT receive message: {}", message);
    }
    private String mergeChunks(String fileId, String fileName) throws IOException {
        log.info("Merging chunks for file: {}", fileId);
        //tempDir=./temp/fileId
        Path tempDir = Paths.get(tempPath, fileId);
        //uploadDir=./uploads
        Path uploadDir = Paths.get(uploadPath);
        //tạo director=uploadDir
        Files.createDirectories(uploadDir);

        // Tạo tên file final với timestamp
        String finalFileName = System.currentTimeMillis() + "_" + fileName;
        //finalPath=./uploads/System.currentTimeMillis()_fileName
        Path finalPath = uploadDir.resolve(finalFileName);

        // Ghép file
        //mở luồng ghi đến file đích
        try (OutputStream os = new FileOutputStream(finalPath.toFile())) {
            //lấy all in file trong temp có name=chunk_
            java.io.File[] chunks = tempDir.toFile().listFiles((dir, name) -> name.startsWith("chunk_"));

            if (chunks != null) {
                // Sắp xếp chunks theo thứ tự
                Arrays.sort(chunks, (a, b) -> {
                    int numA = Integer.parseInt(a.getName().split("_")[1]);
                    int numB = Integer.parseInt(b.getName().split("_")[1]);
                    return Integer.compare(numA, numB);
                });
//đọc từng chunk đưa vào FileInputStream rồi copy vào file OutputStream
                for (java.io.File chunk : chunks) {
                    try (FileInputStream fis = new FileInputStream(chunk)) {
                        IOUtils.copy(fis, os);
                    }
                }
            }
        }

        log.info("File merged successfully: {}", finalPath);
        return finalPath.toString();
    }

    private long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            log.error("Error getting file size: {}", e.getMessage());
            return 0;
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            log.error("Error deleting: {}", e.getMessage());
                        }
                    });
        }
    }


}
