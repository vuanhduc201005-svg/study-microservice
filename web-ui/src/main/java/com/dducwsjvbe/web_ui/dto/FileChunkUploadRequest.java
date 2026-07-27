package com.dducwsjvbe.web_ui.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileChunkUploadRequest {
    @NotBlank
    private String fileId;

    @NotBlank
    private String fileName;

    @NotBlank
    private String articleId;

    @NotBlank
    private String articleName;

    @NotBlank
    private String articleMessage;

    @Min(0)
    private int chunkIndex;

    @Min(1)
    private int totalChunks;
}
