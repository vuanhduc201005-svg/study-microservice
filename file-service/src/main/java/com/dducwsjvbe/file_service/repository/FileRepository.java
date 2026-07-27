package com.dducwsjvbe.file_service.repository;

import com.dducwsjvbe.file_service.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File,Long> {
    File findByArticleId(String articleId);

    File findByFileId(String fileId);
}
