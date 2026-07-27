package com.dducwsjvbe.file_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static reactor.netty.http.HttpConnectionLiveness.log;

@Configuration
@Slf4j
public class FileConfig implements WebMvcConfigurer {
    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadPath).toAbsolutePath();

        log.info("Serving static from: {}", path);
        log.info("Exists: {}", Files.exists(path));

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(path.toUri().toString());
    }

}
/*
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:/app/uploads/");
    }
 */