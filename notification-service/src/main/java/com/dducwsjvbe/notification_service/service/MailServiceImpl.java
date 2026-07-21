package com.dducwsjvbe.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    private String emailFrom;
    @Override
    public void sendMail(String emailTo, String subject, String content, MultipartFile[] files)throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setFrom(emailFrom, "dducwsjvbe");
        helper.setTo(emailTo);
        helper.setSubject(subject);
        helper.setText(content,true);
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(file.getOriginalFilename(), file);
            }
        }
        javaMailSender.send(mimeMessage);
    }
}
