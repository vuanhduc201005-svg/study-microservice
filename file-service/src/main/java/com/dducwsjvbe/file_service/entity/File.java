package com.dducwsjvbe.file_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class File extends AbstracEntity<Long> {
    @Column(name = "file_id",unique = true, nullable = false)
    private String fileId;

    @Column(name = "file_name",unique = true, nullable = false)
    private String fileName;

    @Column(name = "file_path",unique = true, nullable = false)
    private String filePath;

    @Column(name = "article_id",unique = true, nullable = false)
    private String articleId;

    @Column(name = "file_size",nullable = false)
    private Long fileSize;

    @Column(name="is_ready",nullable = false)
    private Boolean isReady;


}
