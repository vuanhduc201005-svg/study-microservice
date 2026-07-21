package com.dducwsjvbe.notification_service.service;

import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

public interface MailService {
    void sendMail(String emailTo, String subject, String content, MultipartFile[] files)throws MessagingException, UnsupportedEncodingException;
}
